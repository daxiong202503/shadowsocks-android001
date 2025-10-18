/*******************************************************************************
 *                                                                             *
 *  Copyright (C) 2024 by VPN Project                                         *
 *                                                                             *
 *  This program is free software: you can redistribute it and/or modify     *
 *  it under the terms of the GNU General Public License as published by     *
 *  the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                      *
 *                                                                             *
 *  This program is distributed in the hope that it will be useful,           *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *  GNU General Public License for more details.                              *
 *                                                                             *
 *  You should have received a copy of the GNU General Public License        *
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 *******************************************************************************/

package com.github.shadowsocks.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.R
import com.github.shadowsocks.api.ApiClient
import com.github.shadowsocks.api.models.RegisterRequest
import kotlinx.coroutines.launch

/**
 * 注册Fragment - 简化版本
 */
class RegisterFragment : Fragment() {
    
    private var onRegisterSuccess: ((String) -> Unit)? = null
    
    fun setOnRegisterSuccessListener(listener: (String) -> Unit) {
        onRegisterSuccess = listener
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI(view)
    }
    
    private fun setupUI(view: View) {
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val btnLogin = view.findViewById<TextView>(R.id.btnLogin)
        
        btnRegister?.setOnClickListener {
            performRegister(view)
        }
        
        btnLogin?.setOnClickListener {
            navigateToLogin()
        }
    }
    
    private fun performRegister(view: View) {
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        
        val username = etUsername?.text.toString().trim()
        val email = etEmail?.text.toString().trim()
        val password = etPassword?.text.toString().trim()
        val confirmPassword = etConfirmPassword?.text.toString().trim()
        
        if (TextUtils.isEmpty(username)) {
            etUsername?.error = "请输入用户名"
            return
        }
        
        if (TextUtils.isEmpty(email)) {
            etEmail?.error = "请输入邮箱"
            return
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword?.error = "请输入密码"
            return
        }
        
        if (password != confirmPassword) {
            etConfirmPassword?.error = "密码不匹配"
            return
        }
        
        btnRegister?.isEnabled = false
        progressBar?.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val request = RegisterRequest(username, email, password)
                
                if (ApiClient.isMockMode()) {
                    // 使用模拟API
                    val response = com.github.shadowsocks.api.MockApiService.register(request)
                    if (response.success) {
                        val registerResponse = response.data
                        if (registerResponse != null) {
                            ApiClient.saveToken(registerResponse.token)
                            ApiClient.saveUserId(registerResponse.user.id)
                            Toast.makeText(context, "注册成功 (模拟模式)", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess?.invoke(registerResponse.token)
                        }
                    } else {
                        Toast.makeText(context, response.message ?: "注册失败", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 使用真实API
                    val response = ApiClient.getApiService().register(request)
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        val registerResponse = response.body()?.data
                        if (registerResponse != null) {
                            ApiClient.saveToken(registerResponse.token)
                            ApiClient.saveUserId(registerResponse.user.id)
                            Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess?.invoke(registerResponse.token)
                        }
                    } else {
                        val errorMessage = response.body()?.message ?: "注册失败"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "网络错误: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                btnRegister?.isEnabled = true
                progressBar?.visibility = View.GONE
            }
        }
    }
    
    private fun navigateToLogin() {
        // 切换到登录页面
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, LoginFragment())
            .addToBackStack(null)
            .commit()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
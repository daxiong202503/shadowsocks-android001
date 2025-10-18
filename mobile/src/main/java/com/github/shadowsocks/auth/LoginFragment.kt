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
import com.github.shadowsocks.api.models.LoginRequest
import kotlinx.coroutines.launch

/**
 * 登录Fragment - 简化版本
 */
class LoginFragment : Fragment() {
    
    private var onLoginSuccess: ((String) -> Unit)? = null
    
    fun setOnLoginSuccessListener(listener: (String) -> Unit) {
        onLoginSuccess = listener
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI(view)
    }
    
    private fun setupUI(view: View) {
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnRegister = view.findViewById<TextView>(R.id.btnRegister)
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        
        btnLogin?.setOnClickListener {
            performLogin(view)
        }
        
        btnRegister?.setOnClickListener {
            navigateToRegister()
        }
    }
    
    private fun performLogin(view: View) {
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        
        val username = etUsername?.text.toString().trim()
        val password = etPassword?.text.toString().trim()
        
        if (TextUtils.isEmpty(username)) {
            etUsername?.error = "请输入用户名"
            return
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword?.error = "请输入密码"
            return
        }
        
        btnLogin?.isEnabled = false
        progressBar?.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val request = LoginRequest(username, password)
                
                if (ApiClient.isMockMode()) {
                    // 使用模拟API
                    val response = com.github.shadowsocks.api.MockApiService.login(request)
                    if (response.success) {
                        val loginResponse = response.data
                        if (loginResponse != null) {
                            ApiClient.saveToken(loginResponse.token)
                            ApiClient.saveUserId(loginResponse.user.id)
                            Toast.makeText(context, "登录成功 (模拟模式)", Toast.LENGTH_SHORT).show()
                            onLoginSuccess?.invoke(loginResponse.token)
                        }
                    } else {
                        Toast.makeText(context, response.message ?: "登录失败", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 使用真实API
                    val response = ApiClient.getApiService().login(request)
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        val loginResponse = response.body()?.data
                        if (loginResponse != null) {
                            ApiClient.saveToken(loginResponse.token)
                            ApiClient.saveUserId(loginResponse.user.id)
                            Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                            onLoginSuccess?.invoke(loginResponse.token)
                        }
                    } else {
                        val errorMessage = response.body()?.message ?: "登录失败"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "网络错误: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                btnLogin?.isEnabled = true
                progressBar?.visibility = View.GONE
            }
        }
    }
    
    private fun navigateToRegister() {
        // 切换到注册页面
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
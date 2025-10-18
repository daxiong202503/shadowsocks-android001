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
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.R
import com.github.shadowsocks.api.ApiClient
import com.github.shadowsocks.api.models.RegisterRequest
import com.github.shadowsocks.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

/**
 * 注册Fragment
 */
class RegisterFragment : Fragment() {
    
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    private var onRegisterSuccess: ((String) -> Unit)? = null
    
    fun setOnRegisterSuccessListener(listener: (String) -> Unit) {
        onRegisterSuccess = listener
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnRegister.setOnClickListener {
            performRegister()
        }
        
        binding.btnLogin.setOnClickListener {
            // 切换到登录页面
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_holder, LoginFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    private fun performRegister() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        
        if (TextUtils.isEmpty(username)) {
            binding.etUsername.error = "请输入用户名"
            return
        }
        
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.error = "请输入邮箱"
            return
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "请输入有效的邮箱地址"
            return
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.error = "请输入密码"
            return
        }
        
        if (password.length < 6) {
            binding.etPassword.error = "密码长度至少6位"
            return
        }
        
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "两次输入的密码不一致"
            return
        }
        
        binding.btnRegister.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val request = RegisterRequest(username, email, password)
                val response = ApiClient.getApiService().register(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val registerResponse = response.body()?.data
                    if (registerResponse != null) {
                        // 保存认证信息
                        ApiClient.saveToken(registerResponse.token)
                        ApiClient.saveUserId(registerResponse.user.id)
                        
                        Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show()
                        onRegisterSuccess?.invoke(registerResponse.token)
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "注册失败"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "网络错误: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnRegister.isEnabled = true
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


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

package com.github.shadowsocks.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.R
import com.github.shadowsocks.api.ApiClient
import kotlinx.coroutines.launch

/**
 * 用户资料Fragment
 */
class UserProfileFragment : Fragment() {
    
    private var onLogout: (() -> Unit)? = null
    
    fun setOnLogoutListener(listener: () -> Unit) {
        onLogout = listener
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        loadUserData()
    }
    
    private fun setupUI() {
        view?.findViewById<Button>(R.id.btnLogout)?.setOnClickListener {
            logout()
        }
        
        view?.findViewById<Button>(R.id.btnRefresh)?.setOnClickListener {
            loadUserData()
        }
    }
    
    private fun loadUserData() {
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val token = ApiClient.getToken()
                if (token.isNullOrEmpty()) {
                    Toast.makeText(context, "未登录", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                if (ApiClient.isMockMode()) {
                    // 使用模拟API
                    val userResponse = com.github.shadowsocks.api.MockApiService.getCurrentUser(token)
                    if (userResponse.success) {
                        val user = userResponse.data
                        if (user != null) {
                            updateUI(user)
                        }
                    }
                    
                    val subscriptionResponse = com.github.shadowsocks.api.MockApiService.getUserSubscription(token)
                    if (subscriptionResponse.success) {
                        val subscription = subscriptionResponse.data
                        if (subscription != null) {
                            updateSubscriptionUI(subscription)
                        }
                    }
                } else {
                    // 使用真实API
                    val userResponse = ApiClient.getApiService().getCurrentUser(token)
                    if (userResponse.isSuccessful && userResponse.body()?.success == true) {
                        val user = userResponse.body()?.data
                        if (user != null) {
                            updateUI(user)
                        }
                    }
                    
                    val subscriptionResponse = ApiClient.getApiService().getUserSubscription(token)
                    if (subscriptionResponse.isSuccessful && subscriptionResponse.body()?.success == true) {
                        val subscription = subscriptionResponse.body()?.data
                        if (subscription != null) {
                            updateSubscriptionUI(subscription)
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "加载用户数据失败: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
            }
        }
    }
    
    private fun updateUI(user: Any) {
        // 更新用户信息UI
        view?.findViewById<TextView>(R.id.tvUsername)?.text = "用户名: ${(user as? Map<String, Any>)?.get("username") ?: "未知"}"
        view?.findViewById<TextView>(R.id.tvEmail)?.text = "邮箱: ${(user as? Map<String, Any>)?.get("email") ?: "未知"}"
        view?.findViewById<TextView>(R.id.tvStatus)?.text = "状态: ${(user as? Map<String, Any>)?.get("status") ?: "未知"}"
    }
    
    private fun updateSubscriptionUI(subscription: Any) {
        // 更新订阅信息UI
        view?.findViewById<TextView>(R.id.tvMemberLevel)?.text = "会员等级: ${(subscription as? Map<String, Any>)?.get("memberLevel") ?: "普通会员"}"
        view?.findViewById<TextView>(R.id.tvMonthlyLimit)?.text = "月度限制: ${(subscription as? Map<String, Any>)?.get("monthlyLimit") ?: "无限制"}"
        view?.findViewById<TextView>(R.id.tvMonthlyUsage)?.text = "月度使用: ${(subscription as? Map<String, Any>)?.get("monthlyUsage") ?: "0"}"
        view?.findViewById<TextView>(R.id.tvMonthlyRemaining)?.text = "月度剩余: ${(subscription as? Map<String, Any>)?.get("monthlyRemainingTraffic") ?: "0"}"
        view?.findViewById<TextView>(R.id.tvExpireDate)?.text = "到期时间: ${(subscription as? Map<String, Any>)?.get("expireDate") ?: "无限制"}"
        view?.findViewById<TextView>(R.id.tvRemainingDays)?.text = "剩余天数: ${(subscription as? Map<String, Any>)?.get("remainingDays") ?: "无限制"}"
    }
    
    private fun logout() {
        ApiClient.clearToken()
        Toast.makeText(context, "已退出登录", Toast.LENGTH_SHORT).show()
        onLogout?.invoke()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.R
import com.github.shadowsocks.api.ApiClient
import com.github.shadowsocks.api.models.UserInfo
import com.github.shadowsocks.api.models.UserSubscription
import com.github.shadowsocks.databinding.FragmentUserProfileBinding
import kotlinx.coroutines.launch

/**
 * 用户资料Fragment
 */
class UserProfileFragment : Fragment() {
    
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    
    private var onLogoutListener: (() -> Unit)? = null
    
    fun setOnLogoutListener(listener: () -> Unit) {
        onLogoutListener = listener
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        loadUserInfo()
    }
    
    private fun setupUI() {
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
        
        binding.btnRefresh.setOnClickListener {
            loadUserInfo()
        }
    }
    
    private fun loadUserInfo() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // 获取用户信息
                val userResponse = ApiClient.getApiService().getCurrentUser("")
                val subscriptionResponse = ApiClient.getApiService().getUserSubscription("")
                
                if (userResponse.isSuccessful && userResponse.body()?.success == true) {
                    val userInfo = userResponse.body()?.data
                    if (userInfo != null) {
                        displayUserInfo(userInfo)
                    }
                }
                
                if (subscriptionResponse.isSuccessful && subscriptionResponse.body()?.success == true) {
                    val subscription = subscriptionResponse.body()?.data
                    if (subscription != null) {
                        displaySubscriptionInfo(subscription)
                    }
                }
                
            } catch (e: Exception) {
                Toast.makeText(context, "加载用户信息失败: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun displayUserInfo(userInfo: UserInfo) {
        binding.tvUsername.text = userInfo.username
        binding.tvEmail.text = userInfo.email
        binding.tvStatus.text = when (userInfo.status) {
            "active" -> "活跃"
            "inactive" -> "未激活"
            else -> userInfo.status
        }
        
        // 显示流量信息
        val totalTraffic = formatTraffic(userInfo.totalTraffic)
        val remainingTraffic = formatTraffic(userInfo.remainingTraffic)
        
        binding.tvTotalTraffic.text = totalTraffic
        binding.tvRemainingTraffic.text = remainingTraffic
        
        // 显示会员信息
        if (userInfo.memberLevelId != null) {
            binding.tvMemberLevel.text = "会员等级: ${userInfo.memberLevelId}"
        } else {
            binding.tvMemberLevel.text = "未设置会员等级"
        }
        
        if (userInfo.memberMonthlyLimit != null) {
            binding.tvMonthlyLimit.text = "每月限流: ${formatTraffic(userInfo.memberMonthlyLimit)}"
        } else {
            binding.tvMonthlyLimit.text = "每月限流: 未设置"
        }
        
        if (userInfo.monthlyUsage != null) {
            binding.tvMonthlyUsage.text = "当月使用: ${formatTraffic(userInfo.monthlyUsage)}"
        } else {
            binding.tvMonthlyUsage.text = "当月使用: 0"
        }
        
        if (userInfo.monthlyRemainingTraffic != null) {
            binding.tvMonthlyRemaining.text = "当月剩余: ${formatTraffic(userInfo.monthlyRemainingTraffic)}"
        } else {
            binding.tvMonthlyRemaining.text = "当月剩余: 未设置"
        }
    }
    
    private fun displaySubscriptionInfo(subscription: UserSubscription) {
        binding.tvExpireDate.text = subscription.expireDate ?: "未设置"
        
        if (subscription.remainingDays != null) {
            binding.tvRemainingDays.text = "剩余 ${subscription.remainingDays} 天"
        } else {
            binding.tvRemainingDays.text = "剩余天数: 未计算"
        }
    }
    
    private fun formatTraffic(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024 * 1024)} GB"
            bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            bytes >= 1024 -> "${bytes / 1024} KB"
            else -> "$bytes B"
        }
    }
    
    private fun performLogout() {
        ApiClient.clearToken()
        Toast.makeText(context, "已退出登录", Toast.LENGTH_SHORT).show()
        onLogoutListener?.invoke()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


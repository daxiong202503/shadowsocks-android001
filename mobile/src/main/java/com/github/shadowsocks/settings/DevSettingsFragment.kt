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

package com.github.shadowsocks.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.shadowsocks.R
import com.github.shadowsocks.api.ApiClient

/**
 * 开发设置Fragment - 简化版本
 */
class DevSettingsFragment : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dev_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI(view)
    }
    
    private fun setupUI(view: View) {
        val switchMockMode = view.findViewById<Switch>(R.id.switchMockMode)
        val btnTestLogin = view.findViewById<Button>(R.id.btnTestLogin)
        val btnClearData = view.findViewById<Button>(R.id.btnClearData)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        
        // 设置初始状态
        switchMockMode?.isChecked = ApiClient.isMockMode()
        updateStatusText(view)
        
        switchMockMode?.setOnCheckedChangeListener { _, isChecked ->
            ApiClient.setMockMode(isChecked)
            updateStatusText(view)
            Toast.makeText(context, if (isChecked) "已启用模拟模式" else "已禁用模拟模式", Toast.LENGTH_SHORT).show()
        }
        
        btnTestLogin?.setOnClickListener {
            testLogin()
        }
        
        btnClearData?.setOnClickListener {
            clearData()
        }
    }
    
    private fun updateStatusText(view: View) {
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        val isMockMode = ApiClient.isMockMode()
        val isLoggedIn = ApiClient.isLoggedIn()
        
        val statusText = buildString {
            appendLine("模拟模式: ${if (isMockMode) "开启" else "关闭"}")
            appendLine("登录状态: ${if (isLoggedIn) "已登录" else "未登录"}")
            if (isLoggedIn) {
                val token = ApiClient.getToken()
                val userId = ApiClient.getUserId()
                appendLine("Token: ${token?.take(20)}...")
                appendLine("用户ID: $userId")
            }
        }
        
        tvStatus?.text = statusText
    }
    
    private fun testLogin() {
        Toast.makeText(context, "测试登录功能", Toast.LENGTH_SHORT).show()
        // 这里可以添加测试登录的逻辑
    }
    
    private fun clearData() {
        ApiClient.clearToken()
        ApiClient.clearUserId()
        updateStatusText(view)
        Toast.makeText(context, "已清除所有数据", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
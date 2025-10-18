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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.shadowsocks.R
import com.github.shadowsocks.api.ApiClient
import com.github.shadowsocks.databinding.FragmentDevSettingsBinding

/**
 * 开发设置Fragment
 */
class DevSettingsFragment : Fragment() {
    
    private var _binding: FragmentDevSettingsBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        loadSettings()
    }
    
    private fun setupUI() {
        binding.switchMockMode.setOnCheckedChangeListener { _, isChecked ->
            ApiClient.setMockMode(isChecked)
            Toast.makeText(context, if (isChecked) "已启用模拟模式" else "已禁用模拟模式", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnClearData.setOnClickListener {
            clearUserData()
        }
        
        binding.btnTestLogin.setOnClickListener {
            testLogin()
        }
    }
    
    private fun loadSettings() {
        binding.switchMockMode.isChecked = ApiClient.isMockMode()
    }
    
    private fun clearUserData() {
        ApiClient.clearToken()
        Toast.makeText(context, "用户数据已清除", Toast.LENGTH_SHORT).show()
    }
    
    private fun testLogin() {
        Toast.makeText(context, "测试登录功能", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


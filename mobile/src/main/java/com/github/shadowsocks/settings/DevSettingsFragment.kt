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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.shadowsocks.R
import com.github.shadowsocks.api.ApiClient

/**
 * 开发设置Fragment
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
        
        setupUI()
        loadSettings()
    }
    
    private fun setupUI() {
        view?.findViewById<Switch>(R.id.switchMockMode)?.setOnCheckedChangeListener { _, isChecked ->
            ApiClient.setMockMode(isChecked)
            Toast.makeText(context, if (isChecked) "已启用模拟模式" else "已禁用模拟模式", Toast.LENGTH_SHORT).show()
        }
        
        view?.findViewById<Button>(R.id.btnClearData)?.setOnClickListener {
            clearUserData()
        }
        
        view?.findViewById<Button>(R.id.btnTestLogin)?.setOnClickListener {
            testLogin()
        }
    }
    
    private fun loadSettings() {
        view?.findViewById<Switch>(R.id.switchMockMode)?.isChecked = ApiClient.isMockMode()
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
    }
}
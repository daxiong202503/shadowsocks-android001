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

package com.github.shadowsocks.api

import android.content.Context
import android.content.SharedPreferences
import com.github.shadowsocks.api.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * API客户端管理器
 */
object ApiClient {
    private const val BASE_URL = "http://localhost:3000/"
    private const val PREFS_NAME = "vpn_client_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_MOCK_MODE = "mock_mode"
    
    private lateinit var apiService: ApiService
    private lateinit var prefs: SharedPreferences
    
    // 模拟模式标志
    private var isMockMode = false
    
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isMockMode = prefs.getBoolean(KEY_MOCK_MODE, false)
        
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        apiService = retrofit.create(ApiService::class.java)
    }
    
    /**
     * 设置模拟模式
     */
    fun setMockMode(enabled: Boolean) {
        isMockMode = enabled
        prefs.edit().putBoolean(KEY_MOCK_MODE, enabled).apply()
    }
    
    /**
     * 检查是否为模拟模式
     */
    fun isMockMode(): Boolean = isMockMode
    
    /**
     * 保存认证令牌
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    /**
     * 获取认证令牌
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    /**
     * 清除认证令牌
     */
    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }
    
    /**
     * 保存用户ID
     */
    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }
    
    /**
     * 获取用户ID
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    
    /**
     * 获取API服务实例
     */
    fun getApiService(): ApiService = apiService
    
    /**
     * 获取带认证头的API服务
     */
    fun getAuthenticatedApiService(): ApiService {
        val token = getToken()
        return if (token != null) {
            // 这里可以创建一个带认证头的API服务实例
            // 或者使用拦截器自动添加认证头
            apiService
        } else {
            apiService
        }
    }
}

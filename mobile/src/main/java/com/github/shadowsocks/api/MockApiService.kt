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

import com.github.shadowsocks.api.models.*
import kotlinx.coroutines.delay
import java.util.*

/**
 * 模拟API服务，用于云构建测试
 */
object MockApiService {
    
    private val mockUsers = mutableMapOf<String, UserInfo>()
    private val mockTokens = mutableMapOf<String, String>()
    
    init {
        // 初始化测试用户
        mockUsers["test@example.com"] = UserInfo(
            id = 1,
            username = "test@example.com",
            email = "test@example.com",
            currentPlanId = 1,
            planExpireDate = "2025-12-31",
            totalTraffic = 100 * 1024 * 1024 * 1024L, // 100GB
            remainingTraffic = 80 * 1024 * 1024 * 1024L, // 80GB
            status = "active",
            memberLevelId = 1,
            memberMonthlyLimit = 50 * 1024 * 1024 * 1024L, // 50GB
            monthlyUsage = 10 * 1024 * 1024 * 1024L, // 10GB
            monthlyRemainingTraffic = 40 * 1024 * 1024 * 1024L // 40GB
        )
    }
    
    /**
     * 模拟登录
     */
    suspend fun login(request: LoginRequest): ApiResponse<LoginResponse> {
        delay(1000) // 模拟网络延迟
        
        return if (request.username == "test@example.com" && request.password == "123456") {
            val token = "mock_token_${System.currentTimeMillis()}"
            val user = mockUsers[request.username]!!
            mockTokens[token] = request.username
            
            ApiResponse(
                success = true,
                data = LoginResponse(token, user),
                message = "登录成功"
            )
        } else {
            ApiResponse(
                success = false,
                data = null,
                message = "用户名或密码错误"
            )
        }
    }
    
    /**
     * 模拟注册
     */
    suspend fun register(request: RegisterRequest): ApiResponse<RegisterResponse> {
        delay(1000) // 模拟网络延迟
        
        return if (mockUsers.containsKey(request.email)) {
            ApiResponse(
                success = false,
                data = null,
                message = "用户已存在"
            )
        } else {
            val userId = mockUsers.size + 1
            val token = "mock_token_${System.currentTimeMillis()}"
            val user = UserInfo(
                id = userId,
                username = request.username,
                email = request.email,
                currentPlanId = null,
                planExpireDate = null,
                totalTraffic = 0,
                remainingTraffic = 0,
                status = "inactive",
                memberLevelId = null,
                memberMonthlyLimit = null,
                monthlyUsage = null,
                monthlyRemainingTraffic = null
            )
            
            mockUsers[request.email] = user
            mockTokens[token] = request.email
            
            ApiResponse(
                success = true,
                data = RegisterResponse(token, user),
                message = "注册成功"
            )
        }
    }
    
    /**
     * 模拟获取当前用户信息
     */
    suspend fun getCurrentUser(token: String): ApiResponse<UserInfo> {
        delay(500) // 模拟网络延迟
        
        val username = mockTokens[token]
        return if (username != null && mockUsers.containsKey(username)) {
            ApiResponse(
                success = true,
                data = mockUsers[username],
                message = "获取用户信息成功"
            )
        } else {
            ApiResponse(
                success = false,
                data = null,
                message = "用户不存在或token无效"
            )
        }
    }
    
    /**
     * 模拟获取用户订阅信息
     */
    suspend fun getUserSubscription(token: String): ApiResponse<UserSubscription> {
        delay(500) // 模拟网络延迟
        
        val username = mockTokens[token]
        return if (username != null && mockUsers.containsKey(username)) {
            val user = mockUsers[username]!!
            val subscription = UserSubscription(
                memberLevel = "高级会员",
                expireDate = user.planExpireDate,
                monthlyLimit = user.memberMonthlyLimit,
                monthlyUsage = user.monthlyUsage,
                monthlyRemainingTraffic = user.monthlyRemainingTraffic,
                remainingDays = 30
            )
            
            ApiResponse(
                success = true,
                data = subscription,
                message = "获取订阅信息成功"
            )
        } else {
            ApiResponse(
                success = false,
                data = null,
                message = "用户不存在或token无效"
            )
        }
    }
    
    /**
     * 模拟获取可用节点
     */
    suspend fun getAvailableNodes(token: String): ApiResponse<List<NodeInfo>> {
        delay(500) // 模拟网络延迟
        
        val username = mockTokens[token]
        return if (username != null && mockUsers.containsKey(username)) {
            val nodes = listOf(
                NodeInfo(
                    id = 1,
                    name = "香港节点1",
                    ip = "1.2.3.4",
                    port = 8388,
                    protocol = "aes-256-gcm",
                    secretKey = "test123456",
                    status = "active",
                    location = "香港"
                ),
                NodeInfo(
                    id = 2,
                    name = "美国节点1",
                    ip = "5.6.7.8",
                    port = 8388,
                    protocol = "aes-256-gcm",
                    secretKey = "test123456",
                    status = "active",
                    location = "美国"
                ),
                NodeInfo(
                    id = 3,
                    name = "日本节点1",
                    ip = "9.10.11.12",
                    port = 8388,
                    protocol = "aes-256-gcm",
                    secretKey = "test123456",
                    status = "active",
                    location = "日本"
                )
            )
            
            ApiResponse(
                success = true,
                data = nodes,
                message = "获取节点列表成功"
            )
        } else {
            ApiResponse(
                success = false,
                data = null,
                message = "用户不存在或token无效"
            )
        }
    }
    
    /**
     * 模拟获取商品列表
     */
    suspend fun getProducts(): ApiResponse<List<ProductInfo>> {
        delay(500) // 模拟网络延迟
        
        val products = listOf(
            ProductInfo(
                id = 1,
                name = "月卡套餐",
                description = "30天有效期，50GB流量",
                price = 29.9,
                durationDays = 30,
                trafficGb = 50,
                monthlyTrafficGb = 50,
                memberLevelId = 1,
                status = "active"
            ),
            ProductInfo(
                id = 2,
                name = "季卡套餐",
                description = "90天有效期，150GB流量",
                price = 79.9,
                durationDays = 90,
                trafficGb = 150,
                monthlyTrafficGb = 50,
                memberLevelId = 1,
                status = "active"
            ),
            ProductInfo(
                id = 3,
                name = "年卡套餐",
                description = "365天有效期，600GB流量",
                price = 299.9,
                durationDays = 365,
                trafficGb = 600,
                monthlyTrafficGb = 50,
                memberLevelId = 1,
                status = "active"
            )
        )
        
        return ApiResponse(
            success = true,
            data = products,
            message = "获取商品列表成功"
        )
    }
    
    /**
     * 模拟创建订单
     */
    suspend fun createOrder(token: String, request: CreateOrderRequest): ApiResponse<OrderInfo> {
        delay(1000) // 模拟网络延迟
        
        val username = mockTokens[token]
        return if (username != null && mockUsers.containsKey(username)) {
            val order = OrderInfo(
                id = Random().nextInt(1000) + 1,
                orderNo = "ORD${System.currentTimeMillis()}",
                userId = mockUsers[username]!!.id,
                productId = request.productId,
                productName = "测试商品",
                totalAmount = 29.9,
                status = "pending",
                createdAt = Date().toString(),
                updatedAt = Date().toString()
            )
            
            ApiResponse(
                success = true,
                data = order,
                message = "订单创建成功"
            )
        } else {
            ApiResponse(
                success = false,
                data = null,
                message = "用户不存在或token无效"
            )
        }
    }
    
    /**
     * 模拟获取用户订单
     */
    suspend fun getUserOrders(token: String): ApiResponse<List<OrderInfo>> {
        delay(500) // 模拟网络延迟
        
        val username = mockTokens[token]
        return if (username != null && mockUsers.containsKey(username)) {
            val orders = listOf(
                OrderInfo(
                    id = 1,
                    orderNo = "ORD123456789",
                    userId = mockUsers[username]!!.id,
                    productId = 1,
                    productName = "月卡套餐",
                    totalAmount = 29.9,
                    status = "completed",
                    createdAt = "2024-01-01",
                    updatedAt = "2024-01-01"
                ),
                OrderInfo(
                    id = 2,
                    orderNo = "ORD987654321",
                    userId = mockUsers[username]!!.id,
                    productId = 2,
                    productName = "季卡套餐",
                    totalAmount = 79.9,
                    status = "pending",
                    createdAt = "2024-01-15",
                    updatedAt = "2024-01-15"
                )
            )
            
            ApiResponse(
                success = true,
                data = orders,
                message = "获取订单列表成功"
            )
        } else {
            ApiResponse(
                success = false,
                data = null,
                message = "用户不存在或token无效"
            )
        }
    }
}


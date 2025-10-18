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

import retrofit2.Response
import retrofit2.http.*
import com.github.shadowsocks.api.models.*

/**
 * API服务接口
 */
interface ApiService {
    
    // 用户认证相关
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<RegisterResponse>>
    
    @GET("api/users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<ApiResponse<UserInfo>>
    
    // 订阅相关
    @GET("api/users/subscription")
    suspend fun getUserSubscription(@Header("Authorization") token: String): Response<ApiResponse<UserSubscription>>
    
    // 节点相关
    @GET("api/nodes")
    suspend fun getAvailableNodes(@Header("Authorization") token: String): Response<ApiResponse<List<NodeInfo>>>
    
    // 商品相关
    @GET("api/products")
    suspend fun getProducts(): Response<ApiResponse<List<ProductInfo>>>
    
    // 订单相关
    @POST("api/orders")
    suspend fun createOrder(@Header("Authorization") token: String, @Body request: CreateOrderRequest): Response<ApiResponse<OrderInfo>>
    
    @GET("api/orders")
    suspend fun getUserOrders(@Header("Authorization") token: String): Response<ApiResponse<List<OrderInfo>>>
}


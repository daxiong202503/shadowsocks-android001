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

package com.github.shadowsocks.api.models

import com.google.gson.annotations.SerializedName

/**
 * API响应基础类
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?
)

/**
 * 登录请求
 */
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

/**
 * 登录响应
 */
data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserInfo
)

/**
 * 注册请求
 */
data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

/**
 * 注册响应
 */
data class RegisterResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserInfo
)

/**
 * 用户信息
 */
data class UserInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("current_plan_id") val currentPlanId: Int?,
    @SerializedName("plan_expire_date") val planExpireDate: String?,
    @SerializedName("total_traffic") val totalTraffic: Long,
    @SerializedName("remaining_traffic") val remainingTraffic: Long,
    @SerializedName("status") val status: String,
    @SerializedName("member_level_id") val memberLevelId: Int?,
    @SerializedName("member_monthly_limit") val memberMonthlyLimit: Long?,
    @SerializedName("monthly_usage") val monthlyUsage: Long?,
    @SerializedName("monthly_remaining_traffic") val monthlyRemainingTraffic: Long?
)

/**
 * 用户订阅信息
 */
data class UserSubscription(
    @SerializedName("member_level") val memberLevel: String?,
    @SerializedName("expire_date") val expireDate: String?,
    @SerializedName("monthly_limit") val monthlyLimit: Long?,
    @SerializedName("monthly_usage") val monthlyUsage: Long?,
    @SerializedName("monthly_remaining_traffic") val monthlyRemainingTraffic: Long?,
    @SerializedName("remaining_days") val remainingDays: Int?
)

/**
 * 节点信息
 */
data class NodeInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("ip") val ip: String,
    @SerializedName("port") val port: Int,
    @SerializedName("protocol") val protocol: String,
    @SerializedName("secret_key") val secretKey: String,
    @SerializedName("status") val status: String,
    @SerializedName("location") val location: String?
)

/**
 * 商品信息
 */
data class ProductInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("duration_days") val durationDays: Int,
    @SerializedName("traffic_gb") val trafficGb: Int,
    @SerializedName("monthly_traffic_gb") val monthlyTrafficGb: Int?,
    @SerializedName("member_level_id") val memberLevelId: Int?,
    @SerializedName("status") val status: String
)

/**
 * 创建订单请求
 */
data class CreateOrderRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("payment_method") val paymentMethod: String = "manual"
)

/**
 * 订单信息
 */
data class OrderInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("order_no") val orderNo: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)


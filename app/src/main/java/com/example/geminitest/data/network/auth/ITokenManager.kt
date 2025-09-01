package com.example.geminitest.data.network.auth
interface ITokenManager {
    suspend fun saveTokens(access: String, refresh: String?, expiresAt: Long)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getExpiresAt(): Long
    suspend fun clearTokens()
}
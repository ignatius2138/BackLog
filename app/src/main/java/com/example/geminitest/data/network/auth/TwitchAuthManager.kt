package com.example.geminitest.data.network.auth

import android.content.Context
import net.openid.appauth.AuthorizationServiceConfiguration
import androidx.core.net.toUri
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TwitchAuthManager(
    private val context: Context,
    val clientId: String,
    private val redirectUri: String,
    private val tokenManager: ITokenManager
) {
    private val serviceConfig = AuthorizationServiceConfiguration(
        "https://id.twitch.tv/oauth2/authorize".toUri(),
        "https://id.twitch.tv/oauth2/token".toUri()
    )

    val authService by lazy { AuthorizationService(context) }

    fun getAuthorizationRequest(): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri.toUri()
        )
            .setScopes("user:read:email")
            .build()
    }

    suspend fun performTokenRequest(authResponse: AuthorizationResponse) {
        val tokenRequest = authResponse.createTokenExchangeRequest()

        val response = suspendCancellableCoroutine<TokenResponse> { continuation ->
            authService.performTokenRequest(tokenRequest) { resp, ex ->
                if (resp != null) continuation.resume(resp)
                else continuation.resumeWithException(ex ?: IllegalStateException("Token request failed"))
            }
        }

        val expiresAt = response.accessTokenExpirationTime ?: (System.currentTimeMillis() + 3600_000)
        tokenManager.saveTokens(
            access = response.accessToken!!,
            refresh = response.refreshToken,
            expiresAt = expiresAt
        )
    }

    suspend fun getValidAccessToken(): String? {
        val accessToken = tokenManager.getAccessToken()
        val expiresAt = tokenManager.getExpiresAt()
        val refreshToken = tokenManager.getRefreshToken()

        if (!accessToken.isNullOrEmpty() && System.currentTimeMillis() < expiresAt - 60_000) {
            return accessToken
        }

        return if (!refreshToken.isNullOrEmpty()) refreshAccessToken(refreshToken)
        else null
    }

    private suspend fun refreshAccessToken(refreshToken: String): String? {
        val request = TokenRequest.Builder(serviceConfig, clientId)
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setRefreshToken(refreshToken)
            .build()

        return try {
            val response = suspendCancellableCoroutine<TokenResponse> { cont ->
                authService.performTokenRequest(request) { resp, ex ->
                    if (resp != null) cont.resume(resp)
                    else cont.resumeWithException(ex ?: IllegalStateException("Token refresh failed"))
                }
            }

            val expiresAt = response.accessTokenExpirationTime ?: (System.currentTimeMillis() + 3600_000)
            tokenManager.saveTokens(
                access = response.accessToken!!,
                refresh = response.refreshToken ?: refreshToken,
                expiresAt = expiresAt
            )
            response.accessToken
        } catch (e: Exception) {
            logout()
            null
        }
    }

    suspend fun logout() = tokenManager.clearTokens()
}
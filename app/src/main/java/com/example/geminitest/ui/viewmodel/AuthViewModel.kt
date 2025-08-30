package com.example.geminitest.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminitest.data.network.auth.TwitchAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

sealed interface AuthState {
    object Loading : AuthState
    object Authenticated : AuthState
    data class NeedsAuth(val authIntent: Intent) : AuthState
    data class AuthError(val message: String) : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val twitchAuthManager: TwitchAuthManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val token = twitchAuthManager.getValidAccessToken()
            if (token != null) {
                _authState.value = AuthState.Authenticated
            } else {
                val authRequest = twitchAuthManager.getAuthorizationRequest()
                val authIntent = twitchAuthManager.authService.getAuthorizationRequestIntent(authRequest)
                _authState.value = AuthState.NeedsAuth(authIntent)
            }
        }
    }

    fun handleAuthorizationResponse(response: AuthorizationResponse?, error: AuthorizationException?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (response != null) {
                try {
                    twitchAuthManager.performTokenRequest(response)
                    _authState.value = AuthState.Authenticated
                } catch (e: Exception) {
                    _authState.value = AuthState.AuthError("Ошибка получения токена: ${e.message}")
                }
            } else {
                _authState.value = AuthState.AuthError("Ошибка авторизации: ${error?.message}")
            }
        }
    }

    fun handleAuthRedirect(intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)
        handleAuthorizationResponse(response, error)
    }
}
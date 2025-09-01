package com.example.geminitest.ui.screen

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.geminitest.ui.viewmodel.AuthState
import com.example.geminitest.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToGameList: () -> Unit,
    authRedirectIntent: Intent?,
    onAuthRedirectIntentConsumed: () -> Unit
){
    val authState by viewModel.authState.collectAsState()

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (data != null) {
            val response = net.openid.appauth.AuthorizationResponse.fromIntent(data)
            val error = net.openid.appauth.AuthorizationException.fromIntent(data)
            viewModel.handleAuthorizationResponse(response, error)
        }
    }

    LaunchedEffect(authRedirectIntent) {
        if (authRedirectIntent != null) {
            viewModel.handleAuthRedirect(authRedirectIntent)
            onAuthRedirectIntentConsumed()
        }
    }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                navigateToGameList()
            }
            is AuthState.NeedsAuth -> {
                authLauncher.launch(state.authIntent)
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = authState) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.AuthError -> Text("Error: ${state.message}")
            else -> {}
        }
    }
}
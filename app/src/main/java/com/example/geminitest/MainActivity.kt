package com.example.geminitest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.geminitest.navigation.AppNavGraph
import com.example.geminitest.ui.theme.GameBacklogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authRedirectIntent = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        authRedirectIntent.value = intent

        setContent {
            GameBacklogTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    authRedirectIntent = authRedirectIntent.value,
                    onAuthRedirectIntentConsumed = { authRedirectIntent.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        authRedirectIntent.value = intent
    }
}
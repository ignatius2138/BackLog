package com.example.geminitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.geminitest.navigation.AppNavGraph
import com.example.geminitest.ui.theme.GameBacklogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GameBacklogTheme {
                val navController = rememberNavController()

                AppNavGraph(
                    navController = navController
                )
            }
        }
    }
}
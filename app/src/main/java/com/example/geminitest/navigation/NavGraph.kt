package com.example.geminitest.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.geminitest.ui.screen.GameListScreen
import com.example.geminitest.ui.screen.AddGameScreen
import com.example.geminitest.ui.screen.AuthScreen
import com.example.geminitest.ui.screen.EditGameScreen
import com.example.geminitest.ui.viewmodel.AddGameViewModel
import com.example.geminitest.ui.viewmodel.GameViewModel

@Composable
fun AppNavGraph(
    authRedirectIntent: Intent?, // Принимаем Intent
    onAuthRedirectIntentConsumed: () -> Unit,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "auth") {

        composable("auth") {
            AuthScreen(
                navigateToGameList = {
                    navController.navigate("gameList") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                },
                authRedirectIntent = authRedirectIntent,
                onAuthRedirectIntentConsumed = onAuthRedirectIntentConsumed
            )
        }

        composable("gameList") {
            GameListScreen(
                navigateToAdd = { navController.navigate("addGame") },
                navigateToEdit = { gameId -> navController.navigate("editGame/$gameId") }
            )
        }
        composable("addGame") {
            AddGameScreen(onGameSaved = { navController.popBackStack() })
        }
        composable(
            route = "editGame/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId") ?: -1
            EditGameScreen(gameId = gameId, onNavigateBack = { navController.popBackStack()})
        }
    }
}

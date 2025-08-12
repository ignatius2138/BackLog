package com.example.geminitest.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.geminitest.ui.screen.GameListScreen
import com.example.geminitest.ui.screen.AddGameScreen
import com.example.geminitest.ui.screen.EditGameScreen
import com.example.geminitest.ui.viewmodel.AddGameViewModel
import com.example.geminitest.ui.viewmodel.GameViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: GameViewModel
) {
    NavHost(navController = navController, startDestination = "gameList") {
        composable("gameList") {
            GameListScreen(viewModel, navController)
        }
        composable("addGame") {
            val addGameViewModel: AddGameViewModel = hiltViewModel()
            AddGameScreen(
                viewModel = addGameViewModel,
                onGameSaved = { navController.popBackStack() }
            )
        }
        composable(
            route = "editGame/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId") ?: -1
            EditGameScreen(viewModel, navController, gameId)
        }
    }
}

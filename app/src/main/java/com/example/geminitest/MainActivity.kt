package com.example.geminitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.geminitest.data.Game
import com.example.geminitest.data.GameDatabase
import com.example.geminitest.data.GameRepository
import com.example.geminitest.ui.theme.GameBacklogTheme
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GameBacklogTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val db = remember { GameDatabase.getDatabase(context) }
                val repository = remember { GameRepository(db.gameDao()) }
                val viewModel: GameViewModel = viewModel(
                    factory = GameViewModelFactory(repository)
                )

                NavHost(navController = navController, startDestination = "gameList") {
                    composable("gameList") {
                        GameListScreen(viewModel, navController)
                    }
                    composable(
                        route = "addGame",
                    ) {
                        AddGameScreen(viewModel = viewModel, navController = navController)
                    }
                    composable(
                        route = "editGame/{gameId}",
                        arguments = listOf(navArgument("gameId") { type = NavType.IntType })
                    ){backStackEntry ->
                        val gameId = backStackEntry.arguments?.getInt("gameId") ?: -1
                        EditGameScreen(viewModel = viewModel, navController = navController, gameId = gameId)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(viewModel: GameViewModel, navController: NavController) {
    val games by viewModel.games.collectAsState()
    val searchText by viewModel.searchText.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Game Backlog",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addGame") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, "Add Game")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(text = "Search") },
            )
            if (games.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(games) { index, game -> // Use itemsIndexed
                        GameRow(index = index + 1, game = game, viewModel = viewModel, navController = navController) // Pass index + 1
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No games yet", style = TextStyle(fontSize = 20.sp))
                }
            }
        }
    }
}

@Composable
fun GameRow(index: Int, game: Game, viewModel: GameViewModel, navController: NavController) { // Add index parameter
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$index", // Display the index (starting from 1)
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = game.name, style = MaterialTheme.typography.displayMedium)
                Text(text = game.genre, style = MaterialTheme.typography.bodyLarge)
            }
            IconButton(onClick = { navController.navigate("editGame/${game.id}") }) { // Use game.id for navigation
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Game", tint = MaterialTheme.colorScheme.primary)
            }

            IconButton(onClick = { viewModel.deleteGame(game) }) { // Use game for deletion
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Game", tint = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(viewModel: GameViewModel, navController: NavController) {
    var gameName by remember { mutableStateOf("") }
    var gameGenre by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Game",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = gameName,
                onValueChange = { gameName = it },
                label = { Text("Game Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = gameGenre,
                onValueChange = { gameGenre = it },
                label = { Text("Genre") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (gameName.isNotBlank() && gameGenre.isNotBlank()) {
                        viewModel.insertGame(gameName, gameGenre)
                        navController.popBackStack() // Go back to the list
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = gameName.isNotBlank() && gameGenre.isNotBlank()
            ) {
                Text("Add Game")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGameScreen(viewModel: GameViewModel, navController: NavController, gameId: Int) {
    val game = viewModel.getGameById(gameId).collectAsState(initial = null).value
    var gameName by remember { mutableStateOf("") }
    var gameGenre by remember { mutableStateOf("") }

    LaunchedEffect(game?.id) {
        game?.let {
            gameName = it.name
            gameGenre = it.genre
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Game",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
    ) { innerPadding ->
        game?.let { currentGame ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = gameName,
                    onValueChange = { gameName = it },
                    label = { Text("Game Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = gameGenre,
                    onValueChange = { gameGenre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (gameName.isNotBlank() && gameGenre.isNotBlank()) {
                            val updatedGame = currentGame.copy(name = gameName, genre = gameGenre)
                            viewModel.updateGame(updatedGame)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = gameName.isNotBlank() && gameGenre.isNotBlank()
                ) {
                    Text("Update Game")
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
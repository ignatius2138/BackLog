package com.example.geminitest.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.geminitest.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGameScreen(gameViewModel: GameViewModel = hiltViewModel(), gameId: Int, onNavigateBack: () -> Unit) {
    val game = gameViewModel.getGameById(gameId).collectAsStateWithLifecycle(initialValue = null).value
    var gameName by remember { mutableStateOf("") }
    var gameGenre by remember { mutableStateOf("") }
    var releaseYear by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coverUrl by remember { mutableStateOf("") }

    LaunchedEffect(game?.id) {
        game?.let {
            gameName = it.name
            gameGenre = it.genre
            releaseYear = it.releaseYear.orEmpty()
            description = it.description.orEmpty()
            coverUrl = it.coverUrl
        }
    }

    val onUpdateClick = remember(gameName, gameGenre, releaseYear, description, coverUrl, game) {
        {
            if (gameName.isNotBlank() && gameGenre.isNotBlank() && game != null) {
                val updatedGame = game.copy(
                    name = gameName,
                    genre = gameGenre,
                    releaseYear = releaseYear,
                    description = description,
                    coverUrl = coverUrl
                )
                gameViewModel.updateGame(updatedGame)
                onNavigateBack()
            }
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
                OutlinedTextField(
                    value = releaseYear,
                    onValueChange = { releaseYear = it },
                    label = { Text("Release Year") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
                Button(
                    onClick = onUpdateClick,
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
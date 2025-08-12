package com.example.geminitest.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.geminitest.ui.viewmodel.AddGameViewModel
import com.example.geminitest.ui.viewmodel.CoverUiState
import com.example.geminitest.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    viewModel: AddGameViewModel,
    onGameSaved: () -> Unit
) {
    val gameName by viewModel.gameName.collectAsStateWithLifecycle()
    val gameGenre by viewModel.gameGenre.collectAsStateWithLifecycle()
    val coverState by viewModel.coverUiState.collectAsStateWithLifecycle()

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
                onValueChange = viewModel::onGameNameChange,
                label = { Text("Game Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = gameGenre,
                onValueChange = viewModel::onGameGenreChange,
                label = { Text("Genre") },
                modifier = Modifier.fillMaxWidth()
            )

            when (coverState) {
                is CoverUiState.Idle -> Text("Enter name of the game to see cover")
                is CoverUiState.Loading -> CircularProgressIndicator()
                is CoverUiState.Empty -> Text("Cover could not be found")
                is CoverUiState.Error -> Text("Error: ${(coverState as CoverUiState.Error).message}")
                is CoverUiState.Success -> {
                    AsyncImage(
                        model = (coverState as CoverUiState.Success).url,
                        contentDescription = "Game cover",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveGame()
                    onGameSaved()
                },
                enabled = gameName.isNotBlank() && gameGenre.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Game")
            }

        }
    }
}
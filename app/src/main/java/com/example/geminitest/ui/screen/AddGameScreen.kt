package com.example.geminitest.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.geminitest.ui.viewmodel.AddGameViewModel
import com.example.geminitest.ui.viewmodel.CoverUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    viewModel: AddGameViewModel = hiltViewModel(),
    onGameSaved: () -> Unit
) {
    val gameName by viewModel.gameName.collectAsStateWithLifecycle()
    val gameGenre by viewModel.gameGenre.collectAsStateWithLifecycle()
    val releaseYear by viewModel.releaseYear.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val coverState by viewModel.coverUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Game", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = (coverState as? CoverUiState.Success)?.url.orEmpty(),
                    contentDescription = "Game cover",
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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

                    OutlinedTextField(
                        value = releaseYear,
                        onValueChange = viewModel::onReleaseYearChange,
                        label = { Text("Release Year") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }

            when (coverState) {
                is CoverUiState.Loading -> CircularProgressIndicator()
                is CoverUiState.Empty -> Text("Cover could not be found")
                is CoverUiState.Error -> Text("Error: ${(coverState as CoverUiState.Error).message}")
                else -> {}
            }

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
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.geminitest.ui.viewmodel.AddGameViewModel
import com.example.geminitest.ui.viewmodel.CoverUiState
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    viewModel: AddGameViewModel = hiltViewModel(),
    onGameSaved: () -> Unit
) {
    val gameName by viewModel.gameName.collectAsStateWithLifecycle()
    val selectedGame by viewModel.selectedGame.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val coverState by viewModel.coverUiState.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Game", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                    model = selectedGame?.coverUrl.orEmpty(),
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
                    // --- Dropdown с управлением фокусом ---
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = gameName,
                            onValueChange = {
                                viewModel.onGameNameChange(it)
                            },
                            label = { Text("Game Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { state ->
                                    isFocused = state.isFocused
                                    expanded = isFocused && searchResults.isNotEmpty()
                                },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            singleLine = true
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            searchResults.forEach { gameData ->
                                DropdownMenuItem(
                                    text = { Text(gameData.name) },
                                    onClick = {
                                        viewModel.selectGame(gameData)
                                        expanded = false
                                        // вместо прямого вызова — флаг
                                        isFocused = true
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = selectedGame?.genre.orEmpty(),
                        onValueChange = viewModel::onGenreChange,
                        label = { Text("Genre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = selectedGame?.releaseYear.orEmpty(),
                        onValueChange = viewModel::onReleaseYearChange,
                        label = { Text("Release Year") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = selectedGame?.description.orEmpty(),
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
                enabled = selectedGame?.name?.isNotBlank() == true &&
                        selectedGame?.genre?.isNotBlank() == true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Game")
            }
        }
    }

    // --- безопасный запрос фокуса через эффект ---
    LaunchedEffect(isFocused) {
        if (isFocused) {
            focusRequester.requestFocus()
        }
    }

    // --- авто-скрытие меню, если результатов нет ---
    LaunchedEffect(searchResults, isFocused) {
        expanded = isFocused && searchResults.isNotEmpty()
    }
}
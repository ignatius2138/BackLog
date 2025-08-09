package com.example.geminitest.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.geminitest.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(viewModel: GameViewModel, navController: NavController) {
    var gameName by remember { mutableStateOf("") }
    var gameGenre by remember { mutableStateOf("") }

    val onGameNameChange = remember { { newName: String -> gameName = newName } }
    val onGameGenreChange = remember { { newGenre: String -> gameGenre = newGenre } }
    val onAddClick = remember(gameName, gameGenre) {
        {
            if (gameName.isNotBlank() && gameGenre.isNotBlank()) {
                viewModel.insertGame(gameName, gameGenre)
                navController.popBackStack()
            }
        }
    }

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
                onValueChange = onGameNameChange,
                label = { Text("Game Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = gameGenre,
                onValueChange = onGameGenreChange,
                label = { Text("Genre") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = gameName.isNotBlank() && gameGenre.isNotBlank()
            ) {
                Text("Add Game")
            }
        }
    }
}
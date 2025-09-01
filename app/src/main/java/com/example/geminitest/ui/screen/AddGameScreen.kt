package com.example.geminitest.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.geminitest.R
import com.example.geminitest.ui.component.GameRow
import com.example.geminitest.ui.viewmodel.AddGameViewModel

@Composable
fun AddGameScreen(
    viewModel: AddGameViewModel = hiltViewModel(),
    onGameSaved: () -> Unit
) {
    val gameName by viewModel.gameName.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedGame by viewModel.selectedGame.collectAsStateWithLifecycle()

    val showResults = searchResults.isNotEmpty() || gameName.isNotBlank()

    val topSpacerWeight by animateFloatAsState(
        targetValue = if (showResults) 0.1f else 1f,
        label = "topSpacerWeight"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(topSpacerWeight))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.enter_the_name_of_the_game),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = gameName,
                onValueChange = { viewModel.onGameNameChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showResults) {
            Spacer(Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(searchResults, key = { _, game -> game.id }) { index, game ->
                    GameRow(
                        index = index + 1,
                        name = game.name,
                        coverUrl = game.coverUrl,
                        genre = game.genre,
                        releaseYear = game.releaseYear,
                        description = game.description,
                        onClick = { viewModel.selectGame(game) },
                        showActions = false,
                        isSelected = selectedGame?.id == game.id
                    )
                }
            }

            selectedGame?.let {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.saveGame()
                        onGameSaved()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.add_game_button_label))
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
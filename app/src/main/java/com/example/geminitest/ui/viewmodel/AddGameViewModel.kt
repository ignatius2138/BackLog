package com.example.geminitest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminitest.data.database.Game
import com.example.geminitest.data.database.RoomGameRepository
import com.example.geminitest.data.network.NetworkGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val gameRepository: RoomGameRepository,
    private val networkRepository: NetworkGameRepository
) : ViewModel() {

    var gameName = MutableStateFlow("")
        private set

    var gameGenre = MutableStateFlow("")
        private set

    var releaseYear = MutableStateFlow("")
        private set

    var description = MutableStateFlow("")
        private set

    private val _coverUiState = MutableStateFlow<CoverUiState>(CoverUiState.Idle)
    val coverUiState: StateFlow<CoverUiState> = _coverUiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameName
                .debounce(2000)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collect { query ->
                    loadGameData(query)
                }
        }
    }

    fun onGameNameChange(newName: String) { gameName.value = newName }
    fun onGameGenreChange(newGenre: String) { gameGenre.value = newGenre }
    fun onReleaseYearChange(newYear: String) { releaseYear.value = newYear }
    fun onDescriptionChange(newDesc: String) { description.value = newDesc }

    private fun loadGameData(name: String) {
        viewModelScope.launch {
            _coverUiState.value = CoverUiState.Loading
            try {
                val gameData = networkRepository.getGameData(name)
                if (gameData != null) {
                    _coverUiState.value = if (
                        gameData.coverUrl.isNotBlank()
                    ) CoverUiState.Success(gameData.coverUrl) else CoverUiState.Empty
                    gameGenre.value = gameData.genre
                    releaseYear.value = gameData.releaseYear
                    description.value = gameData.description
                } else {
                    _coverUiState.value = CoverUiState.Empty
                }
            } catch (e: Exception) {
                _coverUiState.value = CoverUiState.Error(e.message)
            }
        }
    }

    fun saveGame() {
        viewModelScope.launch {
            val game = Game(
                name = gameName.value.trim(),
                genre = gameGenre.value.trim(),
                coverUrl = (coverUiState.value as? CoverUiState.Success)?.url.orEmpty(),
                releaseYear = releaseYear.value.trim(),
                description = description.value.trim()
            )
            if (game.name.isNotBlank() && game.genre.isNotBlank()) {
                gameRepository.insertGame(game)
            }
        }
    }
}

sealed interface CoverUiState {
    object Idle : CoverUiState
    object Loading : CoverUiState
    object Empty : CoverUiState
    data class Success(val url: String) : CoverUiState
    data class Error(val message: String?) : CoverUiState
}

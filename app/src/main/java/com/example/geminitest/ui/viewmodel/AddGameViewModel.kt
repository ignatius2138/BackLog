package com.example.geminitest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminitest.data.database.Game
import com.example.geminitest.data.database.RoomGameRepository
import com.example.geminitest.data.network.GameData
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

    private val _searchResults = MutableStateFlow<List<GameData>>(emptyList())
    val searchResults: StateFlow<List<GameData>> = _searchResults.asStateFlow()

    private val _selectedGame = MutableStateFlow<GameData?>(null)
    val selectedGame: StateFlow<GameData?> = _selectedGame.asStateFlow()

    private val _coverUiState = MutableStateFlow<CoverUiState>(CoverUiState.Idle)
    val coverUiState: StateFlow<CoverUiState> = _coverUiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameName
                .debounce(500)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collect { query ->
                    searchGames(query)
                }
        }
    }

    fun onGameNameChange(newName: String) {
        gameName.value = newName
        _selectedGame.value = _selectedGame.value?.copy(name = newName) ?: _selectedGame.value
    }

    fun onGenreChange(newGenre: String) {
        _selectedGame.value = _selectedGame.value?.copy(genre = newGenre)
    }

    fun onReleaseYearChange(newYear: String) {
        _selectedGame.value = _selectedGame.value?.copy(releaseYear = newYear)
    }

    fun onDescriptionChange(newDesc: String) {
        _selectedGame.value = _selectedGame.value?.copy(description = newDesc)
    }

    fun selectGame(gameData: GameData) {
        _selectedGame.value = gameData
        gameName.value = gameData.name
        _coverUiState.value = if (gameData.coverUrl.isNotBlank()) {
            CoverUiState.Success(gameData.coverUrl)
        } else CoverUiState.Empty
    }

    private fun searchGames(query: String) {
        viewModelScope.launch {
            _coverUiState.value = CoverUiState.Loading
            try {
                val results = networkRepository.searchGames(query)
                _searchResults.value = results
                if (results.isEmpty()) {
                    _coverUiState.value = CoverUiState.Empty
                }
            } catch (e: Exception) {
                _coverUiState.value = CoverUiState.Error(e.message)
            }
        }
    }

    fun saveGame() {
        val gameData = _selectedGame.value ?: return
        viewModelScope.launch {
            val game = Game(
                name = gameData.name.trim(),
                genre = gameData.genre.trim(),
                coverUrl = gameData.coverUrl,
                releaseYear = gameData.releaseYear.trim(),
                description = gameData.description.trim()
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

package com.example.geminitest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminitest.data.database.Game
import com.example.geminitest.data.database.GameRepository
import com.example.geminitest.data.network.GameCoverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val coverRepository: GameCoverRepository
) : ViewModel() {

    var gameName = MutableStateFlow("")
        private set

    var gameGenre = MutableStateFlow("")
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
                    loadCover(query)
                }
        }
    }

    fun onGameNameChange(newName: String) {
        gameName.value = newName
    }

    fun onGameGenreChange(newGenre: String) {
        gameGenre.value = newGenre
    }

    private fun loadCover(name: String) {
        viewModelScope.launch {
            _coverUiState.value = CoverUiState.Loading
            try {
                val url = coverRepository.getImageUrl(name)
                _coverUiState.value = if (url != null) {
                    CoverUiState.Success(url)
                } else {
                    CoverUiState.Empty
                }
            } catch (e: Exception) {
                _coverUiState.value = CoverUiState.Error(e.message)
            }
        }
    }

    fun saveGame() {
        viewModelScope.launch {
            val name = gameName.value.trim()
            val genre = gameGenre.value.trim()
            val coverUrl = (coverUiState.value as? CoverUiState.Success)?.url.orEmpty()
            if (name.isNotBlank() && genre.isNotBlank()) {
                gameRepository.insertGame(Game(name = name, genre = genre, coverUrl = coverUrl))
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

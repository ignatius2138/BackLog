package com.example.geminitest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.geminitest.data.Game
import com.example.geminitest.data.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = combine(_games, _searchText) { games, text ->
        if(text.isBlank()){
            games
        } else {
            games.filter { game ->
                game.name.contains(text, ignoreCase = true) ||
                        game.genre.contains(text, ignoreCase = true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _games.value
    )

    init {
        viewModelScope.launch {
            repository.allGames.collect { gameList ->
                _games.value = gameList
            }
        }
    }
    fun onSearchTextChange(text: String){
        _searchText.value = text
    }

    fun insertGame(name: String, genre: String) {
        viewModelScope.launch {
            val game = Game(name = name, genre = genre)
            repository.insertGame(game)
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            repository.deleteGame(game)
        }
    }

    fun updateGame(game: Game) {
        viewModelScope.launch {
            repository.updateGame(game)
        }
    }

    fun getGameById(gameId: Int): StateFlow<Game?> {
        val gameFlow = MutableStateFlow<Game?>(null)
        viewModelScope.launch {
            repository.getGameById(gameId).collect { game ->
                gameFlow.value = game
            }
        }
        return gameFlow.asStateFlow()
    }
}

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
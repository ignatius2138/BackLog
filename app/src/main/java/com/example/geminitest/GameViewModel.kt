package com.example.geminitest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.geminitest.data.database.Game
import com.example.geminitest.data.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor (private val repository: GameRepository) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = combine(_games, _searchText) { games, text ->
        if (text.isBlank()) {
            games
        } else {
            games.filter { game ->
                game.name.contains(text, ignoreCase = true) ||
                        game.genre.contains(text, ignoreCase = true)
            }
        }
    }.distinctUntilChanged()
        .stateIn(
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

    fun getGameById(gameId: Int): Flow<Game?> {
        return repository.getGameById(gameId)
    }
}
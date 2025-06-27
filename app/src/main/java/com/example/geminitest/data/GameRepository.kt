package com.example.geminitest.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GameRepository @Inject constructor (private val gameDao: GameDao) {

    val allGames: Flow<List<Game>> = gameDao.getAllGames()

    suspend fun insertGame(game: Game) {
        gameDao.insertGame(game)
    }

    suspend fun updateGame(game: Game){
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: Game) {
        gameDao.deleteGame(game)
    }

    fun getGameById(gameId: Int): Flow<Game> {
        return gameDao.getGameById(gameId)
    }
}
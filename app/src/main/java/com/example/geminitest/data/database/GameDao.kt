package com.example.geminitest.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertGame(game: Game)

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("SELECT * FROM game_backlog ORDER BY id ASC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM game_backlog WHERE id = :gameId")
    fun getGameById(gameId: Int): Flow<Game>
}
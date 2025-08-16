package com.example.geminitest.data.network

interface IGameRepository {
    suspend fun getGameData(text: String): GameData?
}
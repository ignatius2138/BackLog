package com.example.geminitest.data.network

interface IGameRepository {
    suspend fun searchGames(query: String): List<GameData>
}
package com.example.geminitest.data.network 

interface IGameCoverRepository {
    suspend fun getImageUrl(text: String): String?
}
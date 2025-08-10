package com.example.geminitest.data.network

import com.example.geminitest.BuildConfig
import io.ktor.client.*

class GameCoverRepository(
    private val client: HttpClient
) : IGameCoverRepository {

    override suspend fun getImageUrl(text: String): String? {
        return try {
            val games = fetchGames(
                query = text,
                accessToken = BuildConfig.IGDB_ACCESS_TOKEN,
                clientId = BuildConfig.IGDB_CLIENT_ID,
                client = client
            )

            games.firstOrNull()?.cover?.imageUrl?.let { url ->
                val adjustedUrl = url.replace("t_thumb", "t_screenshot_huge")
                if (adjustedUrl.startsWith("//")) "https:$adjustedUrl" else adjustedUrl
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
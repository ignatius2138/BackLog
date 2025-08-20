package com.example.geminitest.data.network

import com.example.geminitest.BuildConfig
import io.ktor.client.*
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class NetworkGameRepository @Inject constructor(
    private val client: HttpClient
) : IGameRepository {

    override suspend fun searchGames(query: String): List<GameData> {
        return try {
            val games = fetchGames(
                query = query,
                accessToken = BuildConfig.IGDB_ACCESS_TOKEN,
                clientId = BuildConfig.IGDB_CLIENT_ID,
                client = client
            )

            games.map { game ->
                GameData(
                    name = game.name,
                    coverUrl = game.cover?.url?.let { url ->
                        val adjustedUrl = url.replace("t_thumb", "t_cover_big")
                        if (adjustedUrl.startsWith("//")) "https:$adjustedUrl" else adjustedUrl
                    }.orEmpty(),
                    genre = game.genres?.firstOrNull()?.name.orEmpty(),
                    releaseYear = game.first_release_date?.toYear()?.toString().orEmpty(),
                    description = game.summary.orEmpty()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun Long.toYear(): Int {
        return Instant.ofEpochSecond(this)
            .atZone(ZoneId.systemDefault())
            .year
    }
}

data class GameData(
    val name: String,
    val coverUrl: String,
    val genre: String,
    val releaseYear: String,
    val description: String
)
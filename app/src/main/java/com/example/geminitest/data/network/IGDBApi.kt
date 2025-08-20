package com.example.geminitest.data.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*

const val IGDB_BASE_URL = "https://api.igdb.com/v4/games"
const val FIELD_LIST = "name, cover.url, genres.name, first_release_date, summary"
const val SEARCH_LIMIT = 10
suspend fun fetchGames(
    query: String,
    accessToken: String,
    clientId: String,
    client: HttpClient
): List<Game> {


    val bodyText = """
        fields $FIELD_LIST;
        search "$query";
        limit $SEARCH_LIMIT;
    """.trimIndent()

    return client.post(IGDB_BASE_URL) {
        headers {
            append(HttpHeaders.Authorization, "Bearer $accessToken")
            append("Client-ID", clientId)
        }
        contentType(ContentType.Text.Plain)
        setBody(bodyText)
    }.body()
}
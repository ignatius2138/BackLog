package com.example.geminitest.data.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*

suspend fun fetchGames(
    query: String,
    accessToken: String,
    clientId: String,
    client: HttpClient
): List<Game> {
    val bodyText = """
        fields name, cover.url;
        search "$query";
        limit 1;
    """.trimIndent()

    return client.post("https://api.igdb.com/v4/games") {
        headers {
            append(HttpHeaders.Authorization, "Bearer $accessToken")
            append("Client-ID", clientId)
        }
        contentType(ContentType.Text.Plain)
        setBody(bodyText)
    }.body()
}
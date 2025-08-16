package com.example.geminitest.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val name: String,
    val cover: Cover?,
    val genres: List<Genre>?,
    val first_release_date: Long?,
    val summary: String?
)

@Serializable
data class Cover(
    val url: String
)

@Serializable
data class Genre(
    val name: String
)
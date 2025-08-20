package com.example.geminitest.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val name: String,
    val cover: Cover? = null,
    val genres: List<Genre>? = null,
    val first_release_date: Long? = null,
    val summary: String? = null
)

@Serializable
data class Cover(
    val url: String? = null
)

@Serializable
data class Genre(
    val name: String? = null
)
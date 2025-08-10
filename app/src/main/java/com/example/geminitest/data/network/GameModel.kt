package com.example.geminitest.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: Int,
    val name: String? = null,
    val cover: Cover? = null
)

@Serializable
data class Cover(
    val id: Int? = null,
    @SerialName("url") val imageUrl: String? = null
)
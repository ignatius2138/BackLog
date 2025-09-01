package com.example.geminitest.util

import com.example.geminitest.data.database.Game
import com.example.geminitest.data.network.GameData

fun GameData.toEntity(): Game {
    return Game(
        id = 0,
        name = name.trim(),
        genre = genre.trim(),
        coverUrl = coverUrl.trim(),
        releaseYear = releaseYear.trim(),
        description = description.trim()
    )
}
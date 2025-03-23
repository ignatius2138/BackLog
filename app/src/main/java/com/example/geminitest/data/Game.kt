package com.example.geminitest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_backlog")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val genre: String
)
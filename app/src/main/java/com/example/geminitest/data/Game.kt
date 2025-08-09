package com.example.geminitest.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "game_backlog")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val genre: String
)
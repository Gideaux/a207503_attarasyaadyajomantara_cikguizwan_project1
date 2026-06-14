package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a single user-created Kouleej (quiz).
 *
 * Each instance of this class is a row in the "kouleejes" table.
 * Mirrors the in-memory `Kouleej` data class from Project 1, but is now
 * persisted via Room so it survives app restarts.
 */
@Entity(tableName = "kouleejes")
data class KouleejEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subject: String,
    val questionCount: Int,
    val description: String
)

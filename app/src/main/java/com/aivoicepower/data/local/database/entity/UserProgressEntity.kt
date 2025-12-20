package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val id: String = "default_progress",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: Long? = null,
    val totalMinutes: Int = 0,
    val totalExercises: Int = 0,
    val totalRecordings: Int = 0,
    // Skill levels (0-100)
    val dictionLevel: Int = 0,
    val tempoLevel: Int = 0,
    val intonationLevel: Int = 0,
    val volumeLevel: Int = 0,
    val structureLevel: Int = 0,
    val confidenceLevel: Int = 0,
    val fillerWordsLevel: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val id: String = "default",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: Long? = null,
    val totalMinutes: Int = 0,
    val totalExercises: Int = 0,
    val totalRecordings: Int = 0,
    val dictionLevel: Int = 1,
    val tempoLevel: Int = 1,
    val intonationLevel: Int = 1,
    val volumeLevel: Int = 1,
    val structureLevel: Int = 1,
    val confidenceLevel: Int = 1,
    val fillerWordsLevel: Int = 1,
    val updatedAt: Long = System.currentTimeMillis()
)

package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warmup_completions")
data class WarmupCompletionEntity(
    @PrimaryKey
    val id: String,
    val date: String, // "2024-12-23"
    val category: String, // "articulation", "breathing", "voice"
    val completedAt: Long = System.currentTimeMillis(),
    val exercisesCompleted: Int,
    val totalExercises: Int
)

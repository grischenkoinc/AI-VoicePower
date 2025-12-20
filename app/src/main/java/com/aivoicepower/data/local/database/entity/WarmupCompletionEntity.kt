package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warmup_completions")
data class WarmupCompletionEntity(
    @PrimaryKey
    val id: String, // Format: "date_category" e.g. "2024-12-15_articulation"
    val date: String, // "2024-12-15"
    val category: String, // "articulation", "breathing", "voice", "quick"
    val completedAt: Long = System.currentTimeMillis(),
    val exercisesCompleted: Int = 0,
    val totalExercises: Int = 0
)

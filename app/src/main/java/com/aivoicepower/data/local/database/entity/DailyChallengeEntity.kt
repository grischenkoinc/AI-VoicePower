package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey
    val date: String, // "2024-12-15"
    val challengeId: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val recordingId: String? = null
)

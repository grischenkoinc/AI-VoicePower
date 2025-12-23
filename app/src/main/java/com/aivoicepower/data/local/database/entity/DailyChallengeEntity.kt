package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey
    val challengeId: String, // format: "challenge_YYYY-MM-DD"
    val date: String, // ISO format: YYYY-MM-DD
    val challengeType: String,
    val title: String,
    val description: String,
    val timeLimit: Int,
    val difficulty: String,
    val completed: Boolean = false,
    val recordingId: String? = null,
    val recordingDurationMs: Long = 0,
    val completedAt: Long? = null // timestamp
)

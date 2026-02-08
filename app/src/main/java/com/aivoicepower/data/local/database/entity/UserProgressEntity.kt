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

    // Skill levels (0-100, Float for fractional tracking)
    val dictionLevel: Float = 1f,
    val tempoLevel: Float = 1f,
    val intonationLevel: Float = 1f,
    val volumeLevel: Float = 1f,
    val structureLevel: Float = 1f,
    val confidenceLevel: Float = 1f,
    val fillerWordsLevel: Float = 1f,

    // Previous levels for progression arrows (↑/↓)
    val lastDictionLevel: Float? = null,
    val lastTempoLevel: Float? = null,
    val lastIntonationLevel: Float? = null,
    val lastVolumeLevel: Float? = null,
    val lastStructureLevel: Float? = null,
    val lastConfidenceLevel: Float? = null,
    val lastFillerWordsLevel: Float? = null,

    // Peak levels for decay floor (skill can't drop below 50% of peak)
    val peakDictionLevel: Float = 1f,
    val peakTempoLevel: Float = 1f,
    val peakIntonationLevel: Float = 1f,
    val peakVolumeLevel: Float = 1f,
    val peakStructureLevel: Float = 1f,
    val peakConfidenceLevel: Float = 1f,
    val peakFillerWordsLevel: Float = 1f,

    // Daily change tracking for ±5 cap enforcement
    val dailyChangeDiction: Float = 0f,
    val dailyChangeTempo: Float = 0f,
    val dailyChangeIntonation: Float = 0f,
    val dailyChangeVolume: Float = 0f,
    val dailyChangeStructure: Float = 0f,
    val dailyChangeConfidence: Float = 0f,
    val dailyChangeFillerWords: Float = 0f,
    val lastDailyResetDate: String? = null,

    val updatedAt: Long = System.currentTimeMillis()
)

package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnostic_results")
data class DiagnosticResultEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val diction: Int,           // 0-100
    val tempo: Int,             // 0-100
    val intonation: Int,        // 0-100
    val volume: Int,            // 0-100
    val structure: Int,         // 0-100
    val confidence: Int,        // 0-100
    val fillerWords: Int,       // 0-100 (100 = no filler words)
    val persuasiveness: Int = 50, // 0-100 (тільки для persuasive task)
    val recommendations: String, // JSON array as string
    val isInitial: Boolean = true // true = first diagnostic, false = re-diagnostic
)

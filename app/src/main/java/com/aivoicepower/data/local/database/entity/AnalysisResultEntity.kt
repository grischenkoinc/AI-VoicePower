package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_results")
data class AnalysisResultEntity(
    @PrimaryKey
    val id: String,
    val exerciseId: String?,       // BaseExercise.id — конкретна вправа
    val exerciseType: String,      // normalized type: "tongue_twister", "reading", etc.
    val overallScore: Int,
    val diction: Int,
    val tempo: Int,
    val intonation: Int,
    val volume: Int,
    val confidence: Int,
    val fillerWords: Int,
    val structure: Int,
    val persuasiveness: Int,
    val tip: String,
    val strengths: String,         // JSON array as string
    val improvements: String,      // JSON array as string
    val divergences: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

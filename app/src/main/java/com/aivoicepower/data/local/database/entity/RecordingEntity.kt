package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey
    val id: String,
    val filePath: String,
    val durationMs: Long,
    val createdAt: Long = System.currentTimeMillis(),
    // Context
    val type: String, // "diagnostic", "exercise", "improvisation"
    val contextId: String? = null, // courseId_lessonId or improvisation type
    val exerciseId: String? = null,
    // Analysis results (nullable until analyzed)
    val transcription: String? = null,
    val isAnalyzed: Boolean = false,
    val analysisJson: String? = null, // Full analysis as JSON
    // Quick access metrics
    val overallScore: Int? = null,
    val dictionScore: Int? = null,
    val tempoScore: Int? = null,
    val intonationScore: Int? = null
)

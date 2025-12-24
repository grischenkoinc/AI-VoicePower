package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey
    val id: String,
    val filePath: String,
    val durationMs: Long,
    val type: String, // "improvisation", "lesson", "diagnostic"
    val contextId: String, // lesson_id, debate_topic_id, etc.
    val transcription: String? = null,
    val isAnalyzed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

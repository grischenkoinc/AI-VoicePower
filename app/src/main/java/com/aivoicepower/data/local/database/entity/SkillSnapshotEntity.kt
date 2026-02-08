package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skill_snapshots")
data class SkillSnapshotEntity(
    @PrimaryKey
    val id: String,
    val date: String,
    val diction: Float,
    val tempo: Float,
    val intonation: Float,
    val volume: Float,
    val structure: Float,
    val confidence: Float,
    val fillerWords: Float,
    val createdAt: Long = System.currentTimeMillis()
)

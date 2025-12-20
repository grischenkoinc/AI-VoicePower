package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String, // Achievement type as string
    val unlockedAt: Long? = null,
    val progress: Int = 0, // For progressive achievements
    val target: Int = 1,   // Target value
    val isUnlocked: Boolean = false
)

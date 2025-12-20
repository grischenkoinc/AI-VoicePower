package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "default_user",
    val name: String? = null,
    val goal: String, // UserGoal enum as string
    val dailyMinutes: Int = 15,
    val isPremium: Boolean = false,
    val hasCompletedOnboarding: Boolean = false,
    val hasCompletedDiagnostic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

package com.aivoicepower.domain.model.user

data class UserProfile(
    val id: String,
    val name: String?,
    val goal: UserGoal,
    val dailyMinutes: Int,
    val createdAt: Long,
    val isPremium: Boolean = false
)

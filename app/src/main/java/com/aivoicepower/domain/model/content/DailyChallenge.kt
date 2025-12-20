package com.aivoicepower.domain.model.content

data class DailyChallenge(
    val id: String,
    val date: String,        // "2024-12-15"
    val title: String,
    val description: String,
    val task: ImprovisationTask,
    val reward: String       // "10 XP", "+1 до streak", etc.
)

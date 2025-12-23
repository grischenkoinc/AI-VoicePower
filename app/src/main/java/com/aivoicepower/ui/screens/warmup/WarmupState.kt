package com.aivoicepower.ui.screens.warmup

data class WarmupState(
    val isLoading: Boolean = true,
    val stats: WarmupStats? = null,
    val categories: List<WarmupCategory> = emptyList(),
    val error: String? = null
)

data class WarmupStats(
    val currentStreak: Int = 0,
    val todayMinutes: Int = 0,
    val totalCompletions: Int = 0,
    val level: Int = 1
)

data class WarmupCategory(
    val id: String,
    val icon: String,
    val title: String,
    val exerciseCount: Int,
    val estimatedMinutes: Int,
    val description: String,
    val lastCompletedDate: String?, // "2024-12-15" або null
    val completionRate: Float // 0.0 - 1.0
)

enum class WarmupCategoryType {
    ARTICULATION,  // Артикуляція
    BREATHING,     // Дихання
    VOICE,         // Голос
    QUICK          // Швидка розминка
}

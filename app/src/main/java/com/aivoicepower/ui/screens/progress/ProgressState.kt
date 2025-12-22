package com.aivoicepower.ui.screens.progress

import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.model.user.SkillType

data class ProgressState(
    val isLoading: Boolean = true,

    // Overall
    val overallLevel: Int = 0, // 0-100
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,

    // Stats
    val totalExercises: Int = 0,
    val totalMinutes: Int = 0,
    val totalRecordings: Int = 0,

    // Skill levels (current)
    val skillLevels: Map<SkillType, Int> = emptyMap(),

    // Progress over time (last 7 days)
    val weeklyProgress: List<DailyProgress> = emptyList(),

    // Recent achievements
    val recentAchievements: List<Achievement> = emptyList(),
    val totalAchievements: Int = 0,
    val unlockedAchievements: Int = 0,

    val error: String? = null
)

data class DailyProgress(
    val date: String, // "2024-12-18"
    val exercises: Int,
    val minutes: Int
)

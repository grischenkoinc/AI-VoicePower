package com.aivoicepower.ui.screens.home

import com.aivoicepower.domain.model.home.*
import java.util.Calendar

private fun getDefaultGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour in 6..11 -> "Доброго ранку! ☀️"
        hour in 12..17 -> "Добрий день! 👋"
        hour in 18..21 -> "Добрий вечір! 🌅"
        else -> "Доброї ночі! 🌙"
    }
}

data class HomeState(
    val userName: String? = null,
    val currentStreak: Int = 0,
    val greeting: String = getDefaultGreeting(),
    val todayPlan: TodayPlan? = null,
    val weekProgress: WeekProgress? = null,
    val quickActions: List<QuickAction> = emptyList(),
    val currentCourse: CurrentCourse? = null,
    val skills: List<Skill> = emptyList(),
    val dailyTip: DailyTip? = null,
    val coachMessage: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    // Analysis limits for free users
    val isPremium: Boolean = true,
    val remainingAnalyses: Int = Int.MAX_VALUE,
    val remainingImprovAnalyses: Int = Int.MAX_VALUE,
    val remainingAiMessages: Int = Int.MAX_VALUE,
    val maxFreeAnalyses: Int = 5,
    val maxFreeImprovAnalyses: Int = 1,
    val maxFreeAiMessages: Int = 10
)

package com.aivoicepower.ui.screens.home

import com.aivoicepower.domain.model.home.*

data class HomeState(
    val userName: String? = null,
    val currentStreak: Int = 0,
    val greeting: String = "Доброго дня",
    val todayPlan: TodayPlan? = null,
    val weekProgress: WeekProgress? = null,
    val quickActions: List<QuickAction> = emptyList(),
    val currentCourse: CurrentCourse? = null,
    val skills: List<Skill> = emptyList(),
    val dailyTip: DailyTip? = null,
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

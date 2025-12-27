package com.aivoicepower.ui.screens.home

import com.aivoicepower.domain.model.home.QuickAction
import com.aivoicepower.domain.model.home.TodayPlan
import com.aivoicepower.domain.model.home.WeekProgress

data class HomeState(
    val userName: String? = null,
    val currentStreak: Int = 0,
    val greeting: String = "Доброго дня",
    val todayPlan: TodayPlan? = null,
    val weekProgress: WeekProgress? = null,
    val quickActions: List<QuickAction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

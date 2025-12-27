package com.aivoicepower.domain.model.home

data class TodayPlan(
    val activities: List<PlanActivity>,
    val recommendedFocus: String
)

data class PlanActivity(
    val id: String,
    val type: ActivityType,
    val title: String,
    val subtitle: String?,
    val estimatedMinutes: Int,
    val isCompleted: Boolean,
    val navigationRoute: String
)

enum class ActivityType {
    WARMUP,
    LESSON,
    IMPROVISATION,
    AI_COACH,
    DIAGNOSTIC,
    DAILY_CHALLENGE
}

data class QuickAction(
    val id: String,
    val title: String,
    val icon: String,
    val route: String
)

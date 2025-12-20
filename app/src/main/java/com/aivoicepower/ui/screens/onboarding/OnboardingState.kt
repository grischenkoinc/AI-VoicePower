package com.aivoicepower.ui.screens.onboarding

import com.aivoicepower.domain.model.user.UserGoal

data class OnboardingState(
    val currentPage: Int = 0,
    val selectedGoal: UserGoal = UserGoal.GENERAL,
    val dailyMinutes: Int = 15,
    val isNavigating: Boolean = false
)

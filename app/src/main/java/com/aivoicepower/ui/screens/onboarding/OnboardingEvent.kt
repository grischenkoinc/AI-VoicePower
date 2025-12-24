package com.aivoicepower.ui.screens.onboarding

import com.aivoicepower.domain.model.user.UserGoal

sealed class OnboardingEvent {
    data class PageChanged(val page: Int) : OnboardingEvent()
    data class GoalSelected(val goal: UserGoal) : OnboardingEvent()
    data class MinutesSelected(val minutes: Int) : OnboardingEvent()
    object NextClicked : OnboardingEvent()
    object BackClicked : OnboardingEvent()
    object StartDiagnosticClicked : OnboardingEvent()
}

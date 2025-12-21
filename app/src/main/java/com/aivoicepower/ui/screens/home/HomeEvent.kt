package com.aivoicepower.ui.screens.home

sealed class HomeEvent {
    object Refresh : HomeEvent()
    data class ActivityClicked(val navigationRoute: String) : HomeEvent()
    object WarmupClicked : HomeEvent()
    object CoursesClicked : HomeEvent()
    object ImprovisationClicked : HomeEvent()
    object ProgressClicked : HomeEvent()
    object AiCoachClicked : HomeEvent()
}

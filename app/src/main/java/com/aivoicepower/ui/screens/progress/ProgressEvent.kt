package com.aivoicepower.ui.screens.progress

sealed class ProgressEvent {
    object Refresh : ProgressEvent()
    object NavigateToCompare : ProgressEvent()
    object NavigateToAchievements : ProgressEvent()
    object NavigateToHistory : ProgressEvent()
}

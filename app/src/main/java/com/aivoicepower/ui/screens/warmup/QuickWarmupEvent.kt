package com.aivoicepower.ui.screens.warmup

sealed class QuickWarmupEvent {
    object StartQuickWarmup : QuickWarmupEvent()
    object CurrentExerciseCompleted : QuickWarmupEvent()
    data class UpdateElapsedTime(val seconds: Int) : QuickWarmupEvent()
    object FinishQuickWarmup : QuickWarmupEvent()
    object DismissCompletionDialog : QuickWarmupEvent()
}

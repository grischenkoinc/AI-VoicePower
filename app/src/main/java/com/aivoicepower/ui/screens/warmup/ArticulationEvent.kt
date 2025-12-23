package com.aivoicepower.ui.screens.warmup

sealed class ArticulationEvent {
    data class ExerciseClicked(val exercise: ArticulationExercise) : ArticulationEvent()
    object ExerciseDialogDismissed : ArticulationEvent()
    object StartTimer : ArticulationEvent()
    object PauseTimer : ArticulationEvent()
    data class TimerTick(val secondsRemaining: Int) : ArticulationEvent()
    object MarkAsCompleted : ArticulationEvent()
    object SkipExercise : ArticulationEvent()
    object FinishWarmup : ArticulationEvent()
}

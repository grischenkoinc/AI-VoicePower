package com.aivoicepower.ui.screens.warmup

sealed class BreathingEvent {
    data class ExerciseClicked(val exercise: BreathingExercise) : BreathingEvent()
    object ExerciseDialogDismissed : BreathingEvent()
    object StartBreathing : BreathingEvent()
    object PauseBreathing : BreathingEvent()
    data class Tick(val elapsedSeconds: Int, val phase: BreathingPhase, val phaseProgress: Float) : BreathingEvent()
    object MarkAsCompleted : BreathingEvent()
    object SkipExercise : BreathingEvent()
}

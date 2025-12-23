package com.aivoicepower.ui.screens.warmup

sealed class VoiceWarmupEvent {
    data class ExerciseClicked(val exercise: VoiceExercise) : VoiceWarmupEvent()
    object ExerciseDialogDismissed : VoiceWarmupEvent()
    object StartTimer : VoiceWarmupEvent()
    object PauseTimer : VoiceWarmupEvent()
    data class TimerTick(val secondsRemaining: Int) : VoiceWarmupEvent()
    object PlayAudioExample : VoiceWarmupEvent()
    object StopAudioExample : VoiceWarmupEvent()
    object MarkAsCompleted : VoiceWarmupEvent()
    object SkipExercise : VoiceWarmupEvent()
}

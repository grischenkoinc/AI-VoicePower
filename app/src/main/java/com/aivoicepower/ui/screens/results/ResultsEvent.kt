package com.aivoicepower.ui.screens.results

sealed class ResultsEvent {
    object PlayRecordingClicked : ResultsEvent()
    object StopPlaybackClicked : ResultsEvent()
    object RetryExerciseClicked : ResultsEvent()
    object NextExerciseClicked : ResultsEvent()
    object BackToCourseClicked : ResultsEvent()
}

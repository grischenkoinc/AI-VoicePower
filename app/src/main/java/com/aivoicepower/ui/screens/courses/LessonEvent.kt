package com.aivoicepower.ui.screens.courses

sealed class LessonEvent {
    // Theory phase
    object StartExercisesClicked : LessonEvent()

    // Exercise phase - Recording
    object StartRecordingClicked : LessonEvent()
    object StopRecordingClicked : LessonEvent()
    object PlayRecordingClicked : LessonEvent()
    object StopPlaybackClicked : LessonEvent()
    object ReRecordClicked : LessonEvent()
    object CompleteExerciseClicked : LessonEvent()

    // Navigation
    object NextExerciseClicked : LessonEvent()
    object PreviousExerciseClicked : LessonEvent()
    object SkipExerciseClicked : LessonEvent()

    // Completion
    object FinishLessonClicked : LessonEvent()
}

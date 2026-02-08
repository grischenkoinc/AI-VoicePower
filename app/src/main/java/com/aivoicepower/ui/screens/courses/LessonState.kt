package com.aivoicepower.ui.screens.courses

import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.domain.model.exercise.LessonExercise
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.model.user.Achievement

data class LessonState(
    val lesson: Lesson? = null,
    val nextLesson: Lesson? = null,
    val currentPhase: LessonPhase = LessonPhase.Theory,
    val currentExerciseIndex: Int = 0,
    val exerciseStates: List<ExerciseState> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val toastMessage: String? = null,
    val isPlaying: Boolean = false,
    val isLastLessonInCourse: Boolean = false,
    val courseName: String = "",
    val courseCompletionBadge: Achievement? = null
)

sealed class LessonPhase {
    object Theory : LessonPhase()              // Show theory
    object Exercise : LessonPhase()            // Execute exercises
    object Completed : LessonPhase()           // All exercises completed
}

data class ExerciseState(
    val exercise: LessonExercise,
    val status: ExerciseStatus,
    val recordingPath: String? = null,
    val recordingDurationMs: Long = 0,
    val analysisResult: VoiceAnalysisResult? = null
)

sealed class ExerciseStatus {
    object NotStarted : ExerciseStatus()
    object Recording : ExerciseStatus()
    object Recorded : ExerciseStatus()
    object Analyzing : ExerciseStatus()
    object ShowingResults : ExerciseStatus()
    object Completed : ExerciseStatus()
}

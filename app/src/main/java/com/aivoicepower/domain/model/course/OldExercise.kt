package com.aivoicepower.domain.model.course

// Temporary compatibility class - will be removed in future phases
// This is the old Exercise structure used by LessonRepositoryImpl
data class Exercise(
    val id: String,
    val title: String,
    val instruction: String,
    val exampleText: String? = null,
    val targetDurationSeconds: Int = 60,
    val type: ExerciseType, // Using the old ExerciseType from this package
    val steps: List<ExerciseStep> = emptyList(),
    val repetitions: Int = 1,
    val difficulty: Difficulty = Difficulty.BEGINNER
)

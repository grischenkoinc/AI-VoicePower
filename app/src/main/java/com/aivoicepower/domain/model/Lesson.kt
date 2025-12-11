package com.aivoicepower.domain.model

data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val category: LessonCategory,
    val durationMinutes: Int,
    val exercises: List<Exercise>,
    val isPremium: Boolean = false,
    val order: Int = 0
)

enum class LessonCategory {
    DICTION,
    BREATHING,
    INTONATION,
    PRESENTATION
}

data class Exercise(
    val id: String,
    val title: String,
    val instruction: String,
    val exampleText: String? = null,
    val targetDurationSeconds: Int = 60,
    val type: ExerciseType,
    val steps: List<ExerciseStep> = emptyList(),
    val repetitions: Int = 1,
    val difficulty: Difficulty = Difficulty.BEGINNER
)

data class ExerciseStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val durationSeconds: Int = 10,
    val imageUrl: String? = null,
    val tips: List<String> = emptyList()
)

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

enum class ExerciseType {
    PRONUNCIATION,
    READING,
    BREATHING,
    ARTICULATION,
    PRESENTATION
}

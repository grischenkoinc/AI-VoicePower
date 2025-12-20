package com.aivoicepower.domain.model.course

import com.aivoicepower.domain.model.exercise.Exercise as NewExercise

// New domain model from Phase 0.5 specification
// Will replace the old Lesson in future phases
data class NewLesson(
    val id: String,
    val courseId: String,
    val dayNumber: Int,
    val title: String,
    val description: String,
    val theory: TheoryContent?,
    val exercises: List<NewExercise>,
    val estimatedMinutes: Int
) {
    val hasTheory: Boolean
        get() = theory != null
}

// Old Lesson structure for compatibility with existing code
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

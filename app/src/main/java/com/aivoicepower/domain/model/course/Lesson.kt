package com.aivoicepower.domain.model.course

import com.aivoicepower.domain.model.exercise.LessonExercise

data class Lesson(
    val id: String,
    val courseId: String,
    val dayNumber: Int = 0,
    val title: String,
    val description: String,
    val theory: TheoryContent? = null,
    val exercises: List<LessonExercise>,
    val estimatedMinutes: Int,
    val order: Int = 0  // Sort order for display
)

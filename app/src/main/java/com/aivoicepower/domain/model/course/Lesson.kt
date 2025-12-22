package com.aivoicepower.domain.model.course

import com.aivoicepower.domain.model.exercise.Exercise

data class Lesson(
    val id: String,
    val courseId: String,
    val dayNumber: Int,
    val title: String,
    val description: String,
    val theory: TheoryContent?,
    val exercises: List<Exercise>,
    val estimatedMinutes: Int
)

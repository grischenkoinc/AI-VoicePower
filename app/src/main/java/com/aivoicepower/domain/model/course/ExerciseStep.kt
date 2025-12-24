package com.aivoicepower.domain.model.course

// Temporary compatibility class - will be removed in future phases
data class ExerciseStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val durationSeconds: Int = 10,
    val imageUrl: String? = null,
    val tips: List<String> = emptyList()
)

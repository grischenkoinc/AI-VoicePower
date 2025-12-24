package com.aivoicepower.domain.model.course

data class ExerciseResult(
    val exerciseId: String,
    val isCompleted: Boolean,
    val score: Int?, // 0-100
    val recordingId: String?,
    val completedAt: Long?
)

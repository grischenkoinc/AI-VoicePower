package com.aivoicepower.domain.model.exercise

import com.aivoicepower.domain.model.user.SkillType

data class Exercise(
    val id: String,
    val type: ExerciseType,
    val title: String,
    val instruction: String,
    val content: ExerciseContent,
    val durationSeconds: Int,
    val targetMetrics: List<SkillType> // Які навички тренує ця вправа
)

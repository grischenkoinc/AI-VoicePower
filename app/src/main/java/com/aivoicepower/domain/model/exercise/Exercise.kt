package com.aivoicepower.domain.model.exercise

import com.aivoicepower.domain.model.course.Difficulty
import com.aivoicepower.domain.model.course.ExerciseStep
import com.aivoicepower.domain.model.user.SkillType

data class Exercise(
    val id: String,
    val type: ExerciseType,
    val title: String,
    val instruction: String,
    val content: ExerciseContent,
    val durationSeconds: Int,
    val targetMetrics: List<SkillType>, // Які навички тренує ця вправа
    // Additional fields for LessonScreen compatibility
    val exampleText: String? = null,
    val steps: List<ExerciseStep> = emptyList(),
    val repetitions: Int = 1,
    val difficulty: Difficulty = Difficulty.BEGINNER
)

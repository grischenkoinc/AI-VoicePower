package com.aivoicepower.domain.model.exercise

import com.aivoicepower.domain.model.course.Difficulty
import com.aivoicepower.domain.model.course.ExerciseStep
import com.aivoicepower.domain.model.user.SkillType

/**
 * Вправи з уроків (курси "Чітке мовлення", "Сила голосу", etc.)
 *
 * Імплементує BaseExercise для єдиної системи аналітики.
 */
data class LessonExercise(
    override val id: String,
    override val title: String,
    override val durationSeconds: Int,
    override val targetMetrics: List<SkillType>, // Які навички тренує ця вправа
    override val requiresRecording: Boolean = true, // Більшість вправ з уроків потребують запису

    // Lesson-specific fields
    val type: ExerciseType,
    val instruction: String, // Детальна інструкція для уроку
    val content: ExerciseContent,
    val exampleText: String? = null,
    val steps: List<ExerciseStep> = emptyList(),
    val repetitions: Int = 1,
    val difficulty: Difficulty = Difficulty.BEGINNER
) : BaseExercise {

    override val description: String
        get() = instruction // Використовуємо instruction як description для BaseExercise

    override fun getExerciseType(): String = "lesson"
}

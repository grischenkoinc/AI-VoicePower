package com.aivoicepower.domain.model.exercise

import com.aivoicepower.domain.model.content.ImprovisationTask
import com.aivoicepower.domain.model.user.SkillType

/**
 * Вправи з імпровізації (випадкова тема, сторітелінг, дебати, продажна презентація)
 *
 * Імплементує BaseExercise для єдиної системи аналітики.
 * Підтримує симуляції з AI Coach (які будуть перенесені в Improv).
 */
data class ImprovisationExercise(
    override val id: String,
    override val title: String,
    override val description: String,
    override val durationSeconds: Int,
    override val targetMetrics: List<SkillType>,
    override val requiresRecording: Boolean = true, // Імпровізація завжди з записом та аналізом

    // Improvisation-specific fields
    val task: ImprovisationTask, // RandomTopic, Storytelling, Debate, SalesPitch
    val preparationSeconds: Int = 30, // Час на підготовку перед початком
    val allowRetry: Boolean = true, // Чи можна повторити вправу
    val difficulty: String = "intermediate" // beginner, intermediate, advanced
) : BaseExercise {

    override fun getExerciseType(): String = "improvisation"

    /**
     * Загальна тривалість вправи (підготовка + виконання)
     */
    val totalDurationSeconds: Int
        get() = preparationSeconds + durationSeconds

    /**
     * Тип завдання для UI (відображення іконок, кольорів)
     */
    val taskType: String
        get() = when (task) {
            is ImprovisationTask.RandomTopic -> "random_topic"
            is ImprovisationTask.Storytelling -> "storytelling"
            is ImprovisationTask.Debate -> "debate"
            is ImprovisationTask.SalesPitch -> "sales_pitch"
        }
}

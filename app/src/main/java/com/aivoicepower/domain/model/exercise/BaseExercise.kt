package com.aivoicepower.domain.model.exercise

import com.aivoicepower.domain.model.user.SkillType

/**
 * Базовий інтерфейс для всіх типів вправ у додатку.
 *
 * Цей інтерфейс об'єднує:
 * - Вправи з уроків (LessonExercise)
 * - Розминку (WarmupExercise)
 * - Імпровізацію (ImprovisationExercise)
 * - Майбутні типи вправ
 *
 * Дозволяє:
 * - Єдину обробку всіх вправ в аналітиці
 * - Знаходити вправи, що покращують конкретну навичку
 * - Зберігати результати всіх типів вправ однаково
 */
interface BaseExercise {
    /**
     * Унікальний ідентифікатор вправи
     */
    val id: String

    /**
     * Назва вправи (відображається користувачу)
     */
    val title: String

    /**
     * Опис вправи / інструкція
     */
    val description: String

    /**
     * Тривалість вправи в секундах
     */
    val durationSeconds: Int

    /**
     * Список навичок (SkillType), які тренує ця вправа.
     * Використовується для аналітики: "які вправи покращили Дикцію?"
     */
    val targetMetrics: List<SkillType>

    /**
     * Чи потребує ця вправа запису аудіо?
     * - true: вправи з уроків, імпровізація (з аналізом)
     * - false: розминка без запису (артикуляційна гімнастика, візуалізація)
     */
    val requiresRecording: Boolean

    /**
     * Тип вправи для розрізнення в базі даних та UI.
     * Повертає: "lesson", "warmup", "improvisation", "diagnostic"
     */
    fun getExerciseType(): String
}

package com.aivoicepower.domain.model.exercise

import com.aivoicepower.domain.model.user.SkillType

/**
 * Вправи для розминки (артикуляція, дихання, голос)
 *
 * Імплементує BaseExercise для єдиної системи аналітики.
 * Більшість вправ розминки НЕ потребують запису аудіо (тільки візуалізація/таймер).
 */
data class WarmupExercise(
    override val id: String,
    override val title: String,
    override val description: String,
    override val durationSeconds: Int,
    override val targetMetrics: List<SkillType> = emptyList(), // За замовчуванням розминка не прив'язана до конкретної навички
    override val requiresRecording: Boolean = false, // Розминка зазвичай без запису

    // Warmup-specific fields
    val category: WarmupCategory,
    val repetitions: Int?,
    val mediaType: WarmupMediaType,
    val mediaUrl: String?,
    val animationType: AnimationType?
) : BaseExercise {

    override fun getExerciseType(): String = "warmup"

    /**
     * Чи потребує ця вправа медіа-контент?
     */
    val requiresMedia: Boolean
        get() = mediaUrl?.isNotBlank() == true || animationType != null
}

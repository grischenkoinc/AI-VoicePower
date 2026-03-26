package com.aivoicepower.ui.screens.courses

import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Lesson

data class CourseDetailState(
    val course: Course? = null,
    val lessonsWithProgress: List<LessonWithProgress> = emptyList(),
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,
    val progressPercent: Int = 0,
    val isPremium: Boolean = false,
    val isUserPremium: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class LessonWithProgress(
    val lesson: Lesson,
    val isCompleted: Boolean,
    val isLocked: Boolean,
    val lockReason: LockReason = LockReason.None,
    val weekNumber: Int
)

sealed class LockReason {
    object None : LockReason()
    object PrerequisiteNotMet : LockReason()
    object PremiumRequired : LockReason()
}

/**
 * Прогресивне відкриття уроків (однакове для Free і Pro):
 * - Уроки 1-3 (індекси 0-2): завжди відкриті
 * - Уроки 4-5 (індекси 3-4): після завершення 1-3
 * - Уроки 6-7 (індекси 5-6): після завершення 4-5
 * - Уроки 8-9 (індекси 7-8): після завершення 6-7 (тільки Pro)
 * - Уроки 10-11 (індекси 9-10): після завершення 8-9 (тільки Pro)
 * - ... і так далі: кожна наступна пара після попередньої
 * Free-користувачі: урок 8+ заблоковано як PremiumRequired (незалежно від прогресу).
 */
fun getLessonLockReason(
    lessonIndex: Int,
    isUserPremium: Boolean,
    completedIndices: Set<Int>
): LockReason = when {
    lessonIndex in 0..2 -> LockReason.None
    lessonIndex >= 7 && !isUserPremium -> LockReason.PremiumRequired
    else -> {
        // Sequential unlock: lessons 3-4 require 0-2; each subsequent pair requires the previous pair.
        // Group formula for index i >= 3:
        //   batchStart = 3 + ((i - 3) / 2) * 2
        //   prereq = if batchStart == 3 then 0..2 else (batchStart-2)..(batchStart-1)
        val prereqRange = if (lessonIndex in 3..4) {
            0..2
        } else {
            val batchStart = 3 + ((lessonIndex - 3) / 2) * 2
            (batchStart - 2)..(batchStart - 1)
        }
        if (prereqRange.all { it in completedIndices }) LockReason.None
        else LockReason.PrerequisiteNotMet
    }
}

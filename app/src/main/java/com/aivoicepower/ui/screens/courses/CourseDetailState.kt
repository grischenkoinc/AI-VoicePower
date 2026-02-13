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
 * Прогресивне відкриття уроків:
 * - Уроки 1-3 (індекси 0-2): завжди відкриті
 * - Уроки 4-5 (індекси 3-4): після завершення 1-3
 * - Уроки 6-7 (індекси 5-6): після завершення 4-5
 * - Урок 8+ (індекси 7+): тільки Pro, послідовно після попереднього
 */
fun getLessonLockReason(
    lessonIndex: Int,
    isUserPremium: Boolean,
    completedIndices: Set<Int>
): LockReason = when {
    lessonIndex in 0..2 -> LockReason.None
    lessonIndex in 3..4 -> {
        if ((0..2).all { it in completedIndices }) LockReason.None
        else LockReason.PrerequisiteNotMet
    }
    lessonIndex in 5..6 -> {
        if ((3..4).all { it in completedIndices }) LockReason.None
        else LockReason.PrerequisiteNotMet
    }
    lessonIndex >= 7 -> when {
        !isUserPremium -> LockReason.PremiumRequired
        lessonIndex == 7 -> {
            if ((0..6).all { it in completedIndices }) LockReason.None
            else LockReason.PrerequisiteNotMet
        }
        else -> {
            if ((lessonIndex - 1) in completedIndices) LockReason.None
            else LockReason.PrerequisiteNotMet
        }
    }
    else -> LockReason.None
}

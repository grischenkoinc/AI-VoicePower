package com.aivoicepower.utils.constants

/**
 * Ліміти для безкоштовних користувачів
 */
object FreeTierLimits {
    // AI Coach
    const val FREE_MESSAGES_PER_DAY = 10

    // Improvisation
    const val FREE_IMPROVISATIONS_PER_DAY = 3

    // Courses
    const val FREE_LESSONS_PER_COURSE = 7

    // Re-diagnostic
    const val FREE_DIAGNOSTICS = 1

    // Check if within limit
    fun isWithinMessageLimit(count: Int): Boolean = count < FREE_MESSAGES_PER_DAY
    fun isWithinImprovisationLimit(count: Int): Boolean = count < FREE_IMPROVISATIONS_PER_DAY
    fun isWithinLessonLimit(lessonIndex: Int): Boolean = lessonIndex < FREE_LESSONS_PER_COURSE
}

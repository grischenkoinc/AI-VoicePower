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

    // AI Analysis (voice recording analysis via Gemini)
    const val FREE_ANALYSES_PER_DAY = 5
    const val FREE_AD_ANALYSES_PER_DAY = 3
    const val FREE_IMPROV_ANALYSES_PER_DAY = 1
    const val FREE_AD_IMPROV_ANALYSES_PER_DAY = 1

    // Check if within limit
    fun isWithinMessageLimit(count: Int): Boolean = count < FREE_MESSAGES_PER_DAY
    fun isWithinImprovisationLimit(count: Int): Boolean = count < FREE_IMPROVISATIONS_PER_DAY
    fun isWithinLessonLimit(lessonIndex: Int): Boolean = lessonIndex < FREE_LESSONS_PER_COURSE
    fun isWithinAnalysisLimit(usedAnalyses: Int): Boolean = usedAnalyses < FREE_ANALYSES_PER_DAY
    fun isWithinAdAnalysisLimit(usedAdAnalyses: Int): Boolean = usedAdAnalyses < FREE_AD_ANALYSES_PER_DAY
    fun isWithinImprovAnalysisLimit(usedImprovAnalyses: Int): Boolean = usedImprovAnalyses < FREE_IMPROV_ANALYSES_PER_DAY
    fun isWithinAdImprovAnalysisLimit(usedAdImprovAnalyses: Int): Boolean = usedAdImprovAnalyses < FREE_AD_IMPROV_ANALYSES_PER_DAY
}

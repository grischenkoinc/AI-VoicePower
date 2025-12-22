package com.aivoicepower.utils

import com.aivoicepower.ui.screens.premium.PaywallSource
import com.aivoicepower.utils.constants.FreeTierLimits

/**
 * Utility object for checking premium access and limits
 */
object PremiumChecker {

    /**
     * Check if user can access a specific lesson
     */
    fun canAccessLesson(
        isPremium: Boolean,
        lessonIndex: Int // 0-based index
    ): Boolean {
        return isPremium || lessonIndex < FreeTierLimits.FREE_LESSONS_PER_COURSE
    }

    /**
     * Check if user can start improvisation
     */
    fun canStartImprovisation(
        isPremium: Boolean,
        sessionsToday: Int
    ): Boolean {
        return isPremium || sessionsToday < FreeTierLimits.FREE_IMPROVISATIONS_PER_DAY
    }

    /**
     * Check if user can send AI message
     */
    fun canSendAiMessage(
        isPremium: Boolean,
        messagesToday: Int
    ): Boolean {
        return isPremium || messagesToday < FreeTierLimits.FREE_MESSAGES_PER_DAY
    }

    /**
     * Check if user can do another diagnostic
     */
    fun canDoDiagnostic(
        isPremium: Boolean,
        diagnosticCount: Int
    ): Boolean {
        return isPremium || diagnosticCount < FreeTierLimits.FREE_DIAGNOSTICS
    }

    /**
     * Get remaining free lessons for a course
     */
    fun getRemainingFreeLessons(
        isPremium: Boolean,
        completedLessons: Int
    ): Int {
        return if (isPremium) {
            Int.MAX_VALUE
        } else {
            (FreeTierLimits.FREE_LESSONS_PER_COURSE - completedLessons).coerceAtLeast(0)
        }
    }

    /**
     * Get remaining free improvisations today
     */
    fun getRemainingImprovisations(
        isPremium: Boolean,
        sessionsToday: Int
    ): Int {
        return if (isPremium) {
            Int.MAX_VALUE
        } else {
            (FreeTierLimits.FREE_IMPROVISATIONS_PER_DAY - sessionsToday).coerceAtLeast(0)
        }
    }

    /**
     * Get remaining free AI messages today
     */
    fun getRemainingAiMessages(
        isPremium: Boolean,
        messagesToday: Int
    ): Int {
        return if (isPremium) {
            Int.MAX_VALUE
        } else {
            (FreeTierLimits.FREE_MESSAGES_PER_DAY - messagesToday).coerceAtLeast(0)
        }
    }

    /**
     * Get paywall source for analytics
     */
    fun getPaywallSource(reason: String): PaywallSource {
        return when (reason) {
            "course_locked" -> PaywallSource.COURSE_LOCKED
            "improv_limit" -> PaywallSource.IMPROV_LIMIT
            "ai_limit" -> PaywallSource.AI_COACH_LIMIT
            "diagnostic_limit" -> PaywallSource.DIAGNOSTIC_LIMIT
            "settings" -> PaywallSource.SETTINGS
            "achievement" -> PaywallSource.ACHIEVEMENT
            else -> PaywallSource.UNKNOWN
        }
    }

    /**
     * Get formatted limit message
     */
    fun getLimitMessage(source: PaywallSource): String {
        return when (source) {
            PaywallSource.COURSE_LOCKED ->
                "Безкоштовний доступ: ${FreeTierLimits.FREE_LESSONS_PER_COURSE} уроків на курс"
            PaywallSource.IMPROV_LIMIT ->
                "Безкоштовний ліміт: ${FreeTierLimits.FREE_IMPROVISATIONS_PER_DAY} сесії на день"
            PaywallSource.AI_COACH_LIMIT ->
                "Безкоштовний ліміт: ${FreeTierLimits.FREE_MESSAGES_PER_DAY} повідомлень на день"
            PaywallSource.DIAGNOSTIC_LIMIT ->
                "Безкоштовна діагностика: ${FreeTierLimits.FREE_DIAGNOSTICS} раз"
            else -> ""
        }
    }
}

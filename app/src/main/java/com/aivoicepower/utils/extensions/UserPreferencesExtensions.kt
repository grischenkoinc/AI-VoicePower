package com.aivoicepower.utils.extensions

import com.aivoicepower.data.local.datastore.UserPreferences
import com.aivoicepower.utils.constants.FreeTierLimits

/**
 * Extension functions для перевірки доступу
 */

fun UserPreferences.canSendMessage(): Boolean {
    return isPremium || FreeTierLimits.isWithinMessageLimit(freeMessagesToday)
}

fun UserPreferences.canStartImprovisation(): Boolean {
    return isPremium || FreeTierLimits.isWithinImprovisationLimit(freeImprovisationsToday)
}

fun UserPreferences.canAccessLesson(lessonIndex: Int): Boolean {
    return isPremium || FreeTierLimits.isWithinLessonLimit(lessonIndex)
}

fun UserPreferences.remainingFreeMessages(): Int {
    return if (isPremium) Int.MAX_VALUE
    else (FreeTierLimits.FREE_MESSAGES_PER_DAY - freeMessagesToday).coerceAtLeast(0)
}

fun UserPreferences.remainingFreeImprovisations(): Int {
    return if (isPremium) Int.MAX_VALUE
    else (FreeTierLimits.FREE_IMPROVISATIONS_PER_DAY - freeImprovisationsToday).coerceAtLeast(0)
}

package com.aivoicepower.domain.model.user

data class Achievement(
    val id: String,
    val category: AchievementCategory,
    val title: String,
    val description: String,
    val icon: String,
    val unlockedAt: Long?,
    val progress: Int?,
    val target: Int?
) {
    val isUnlocked: Boolean
        get() = unlockedAt != null

    val progressPercentage: Int?
        get() = if (progress != null && target != null && target > 0) {
            ((progress.toFloat() / target) * 100).toInt().coerceIn(0, 100)
        } else null
}

enum class AchievementCategory(val displayName: String) {
    COURSE_COMPLETION("Курси"),
    STREAK("Серії"),
    PRACTICE_TIME("Час практики"),
    RECORDINGS("Записи"),
    SPECIAL("Особливі")
}

package com.aivoicepower.domain.model.user

data class Achievement(
    val id: String,
    val type: AchievementType,
    val title: String,
    val description: String,
    val iconRes: Int,
    val unlockedAt: Long?,
    val progress: Int?,      // Для прогресивних досягнень
    val target: Int?,        // Цільове значення
    val icon: String = "",   // Emoji icon for stub repository
    val tier: String = "bronze"  // Achievement tier (bronze, silver, gold, platinum)
) {
    val isUnlocked: Boolean
        get() = unlockedAt != null

    val progressPercentage: Int?
        get() = if (progress != null && target != null && target > 0) {
            ((progress.toFloat() / target) * 100).toInt().coerceIn(0, 100)
        } else null
}

enum class AchievementType {
    // Streak
    STREAK_7,
    STREAK_30,
    STREAK_100,

    // Курси
    FIRST_COURSE,
    THREE_COURSES,
    ALL_COURSES,

    // Навички
    DICTION_90,
    TEMPO_MASTER,
    EMOTION_ACTOR,
    ZERO_FILLERS,

    // Імпровізація
    IMPROVISER_50,
    DEBATER_20,
    SALESMAN_30,
    STORYTELLER_20,

    // Особливі
    EARLY_BIRD,      // Заняття до 8:00
    NIGHT_OWL,       // Заняття після 22:00
    BREAKTHROUGH;    // Покращення на 20+ балів за місяць

    fun getTitle(): String {
        return when (this) {
            STREAK_7 -> "Тиждень практики"
            STREAK_30 -> "Місяць постійності"
            STREAK_100 -> "Майстер дисципліни"
            FIRST_COURSE -> "Перший крок"
            THREE_COURSES -> "Різнобічний"
            ALL_COURSES -> "Експерт"
            DICTION_90 -> "Ідеальна дикція"
            TEMPO_MASTER -> "Майстер темпу"
            EMOTION_ACTOR -> "Емоційний актор"
            ZERO_FILLERS -> "Чисте мовлення"
            IMPROVISER_50 -> "Імпровізатор"
            DEBATER_20 -> "Полеміст"
            SALESMAN_30 -> "Майстер продажів"
            STORYTELLER_20 -> "Оповідач"
            EARLY_BIRD -> "Рання пташка"
            NIGHT_OWL -> "Нічна сова"
            BREAKTHROUGH -> "Прорив"
        }
    }
}

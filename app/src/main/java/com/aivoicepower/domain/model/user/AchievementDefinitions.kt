package com.aivoicepower.domain.model.user

data class AchievementDef(
    val id: String,
    val category: AchievementCategory,
    val title: String,
    val description: String,
    val icon: String,
    val target: Int? = null
)

object AchievementDefinitions {

    val streak = listOf(
        AchievementDef("streak_3", AchievementCategory.STREAK, "3 дні поспіль", "Займайтеся 3 дні підряд", "\uD83D\uDD25", 3),
        AchievementDef("streak_7", AchievementCategory.STREAK, "Тижнева серія", "Займайтеся 7 днів підряд", "\uD83D\uDD25", 7),
        AchievementDef("streak_14", AchievementCategory.STREAK, "Двотижнева серія", "Займайтеся 14 днів підряд", "\uD83D\uDD25", 14),
        AchievementDef("streak_30", AchievementCategory.STREAK, "Місячна серія", "Займайтеся 30 днів підряд", "\uD83D\uDD25", 30),
        AchievementDef("streak_60", AchievementCategory.STREAK, "60 днів поспіль", "Неймовірна дисципліна!", "\uD83D\uDD25", 60),
        AchievementDef("streak_100", AchievementCategory.STREAK, "100 днів поспіль", "Ви — легенда!", "\uD83D\uDD25", 100)
    )

    val practiceTime = listOf(
        AchievementDef("time_1h", AchievementCategory.PRACTICE_TIME, "1 година практики", "Накопичіть 1 годину практики", "⏱\uFE0F", 60),
        AchievementDef("time_5h", AchievementCategory.PRACTICE_TIME, "5 годин практики", "Серйозний підхід!", "⏱\uFE0F", 300),
        AchievementDef("time_10h", AchievementCategory.PRACTICE_TIME, "10 годин практики", "Справжня відданість!", "⏱\uFE0F", 600),
        AchievementDef("time_25h", AchievementCategory.PRACTICE_TIME, "25 годин практики", "Ви — професіонал!", "⏱\uFE0F", 1500),
        AchievementDef("time_50h", AchievementCategory.PRACTICE_TIME, "50 годин практики", "Неймовірна наполегливість!", "⏱\uFE0F", 3000)
    )

    val recordings = listOf(
        AchievementDef("rec_1", AchievementCategory.RECORDINGS, "Перший запис", "Зробіть перший запис", "\uD83C\uDF99\uFE0F", 1),
        AchievementDef("rec_10", AchievementCategory.RECORDINGS, "10 записів", "Зробіть 10 записів", "\uD83C\uDF99\uFE0F", 10),
        AchievementDef("rec_25", AchievementCategory.RECORDINGS, "25 записів", "Зробіть 25 записів", "\uD83C\uDF99\uFE0F", 25),
        AchievementDef("rec_50", AchievementCategory.RECORDINGS, "50 записів", "Зробіть 50 записів", "\uD83C\uDF99\uFE0F", 50),
        AchievementDef("rec_100", AchievementCategory.RECORDINGS, "100 записів", "Зробіть 100 записів", "\uD83C\uDF99\uFE0F", 100)
    )

    val special = listOf(
        AchievementDef("first_diagnostic", AchievementCategory.SPECIAL, "Перша діагностика", "Пройдіть першу діагностику", "\uD83E\uDE7A"),
        AchievementDef("all_skills_40", AchievementCategory.SPECIAL, "Всебічний розвиток", "Усі 7 навичок вище 40", "\uD83C\uDFAF"),
        AchievementDef("all_skills_60", AchievementCategory.SPECIAL, "Баланс", "Усі 7 навичок вище 60", "⚖\uFE0F"),
        AchievementDef("breakthrough", AchievementCategory.SPECIAL, "Прорив", "Будь-яка навичка +20 за тиждень", "\uD83D\uDE80"),
        AchievementDef("early_bird", AchievementCategory.SPECIAL, "Рання пташка", "Запис між 4:30 та 8:00", "☀\uFE0F"),
        AchievementDef("night_owl", AchievementCategory.SPECIAL, "Нічна сова", "Запис між 22:00 та 4:29", "\uD83C\uDF19"),
        AchievementDef("marathon", AchievementCategory.SPECIAL, "Марафонець", "Запис довший за 5 хвилин", "\uD83C\uDFC3"),
        AchievementDef("polyglot", AchievementCategory.SPECIAL, "Поліглот вправ", "Спробуйте 8+ типів вправ", "\uD83C\uDF0D"),
        AchievementDef("productive_day", AchievementCategory.SPECIAL, "Продуктивний день", "5+ записів за один день", "⚡"),
        AchievementDef("summit", AchievementCategory.SPECIAL, "На вершині", "Будь-яка навичка досягла 90+", "\uD83D\uDC51")
    )

    val all: List<AchievementDef> = streak + practiceTime + recordings + special

    fun getById(id: String): AchievementDef? = all.find { it.id == id }
}

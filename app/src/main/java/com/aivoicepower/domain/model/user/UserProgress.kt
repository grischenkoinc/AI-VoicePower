package com.aivoicepower.domain.model.user

data class UserProgress(
    val userId: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalMinutes: Int,
    val totalExercises: Int,
    val totalRecordings: Int,
    val lastActivityDate: Long?,
    val skillLevels: Map<SkillType, Int>, // 0-100 для кожної навички
    val achievements: List<String>
) {
    /**
     * Обчислює загальний рівень користувача (0-100)
     */
    fun calculateOverallLevel(): Int {
        if (skillLevels.isEmpty()) return 0
        return skillLevels.values.average().toInt()
    }

    /**
     * Повертає найсильніші навички
     */
    fun getTopSkills(count: Int = 3): List<SkillType> {
        return skillLevels.entries
            .sortedByDescending { it.value }
            .take(count)
            .map { it.key }
    }

    /**
     * Повертає навички, що потребують покращення
     */
    fun getWeakSkills(count: Int = 3): List<SkillType> {
        return skillLevels.entries
            .sortedBy { it.value }
            .take(count)
            .map { it.key }
    }
}

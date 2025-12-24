package com.aivoicepower.data.chat

/**
 * User context for AI Coach personalization
 *
 * TODO Phase 6: Replace with actual domain models when implemented
 */
data class ConversationContext(
    val userName: String? = null,
    val userGoal: String? = null,
    val skillLevels: Map<String, Int> = emptyMap(), // skill name to level (0-100)
    val weakestSkills: List<Pair<String, Int>> = emptyList(), // skill name to level
    val currentStreak: Int = 0,
    val totalExercises: Int = 0,
    val recentActivity: String = "" // Text description of activity
) {

    /**
     * Generates text context for system prompt
     */
    fun toPromptContext(): String {
        val parts = mutableListOf<String>()

        userName?.let {
            parts.add("Ім'я: $it")
        }

        userGoal?.let {
            parts.add("Ціль користувача: $it")
        }

        if (currentStreak > 0) {
            parts.add("Поточний streak: $currentStreak днів")
        }

        if (totalExercises > 0) {
            parts.add("Загалом вправ: $totalExercises")
        }

        if (skillLevels.isNotEmpty()) {
            val skillsText = skillLevels.entries
                .take(4)
                .joinToString(", ") { (skill, level) ->
                    "$skill: $level/100"
                }
            parts.add("Рівень навичок: $skillsText")
        }

        if (weakestSkills.isNotEmpty()) {
            val weakest = weakestSkills.take(3).joinToString(", ") { (skill, level) ->
                "$skill ($level/100)"
            }
            parts.add("Слабкі місця: $weakest")
        }

        if (recentActivity.isNotBlank()) {
            parts.add("Недавня активність: $recentActivity")
        }

        return parts.joinToString("\n")
    }

    companion object {
        /**
         * Creates empty context for new users
         */
        fun empty(): ConversationContext {
            return ConversationContext(
                userName = null,
                userGoal = "Покращення мовлення",
                recentActivity = "Користувач щойно почав"
            )
        }
    }
}

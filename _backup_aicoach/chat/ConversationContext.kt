package com.aivoicepower.domain.model.chat

import com.aivoicepower.domain.model.user.DiagnosticResult
import com.aivoicepower.domain.model.user.UserGoal
import com.aivoicepower.domain.model.user.UserProgress
import com.aivoicepower.domain.model.user.UserProfile

/**
 * User context for AI Coach personalization
 */
data class ConversationContext(
    val userProfile: UserProfile?,
    val userProgress: UserProgress?,
    val diagnosticResult: DiagnosticResult?,
    val weakestSkills: List<Pair<String, Int>>, // skill name to level
    val recentActivity: String // Text description of activity
) {

    /**
     * Generates text context for system prompt
     */
    fun toPromptContext(): String {
        val parts = mutableListOf<String>()

        userProfile?.let { profile ->
            parts.add("Ціль користувача: ${formatGoal(profile.goal)}")
            parts.add("Щоденна ціль: ${profile.dailyMinutes} хвилин")
        }

        userProgress?.let { progress ->
            parts.add("Поточний streak: ${progress.currentStreak} днів")
            parts.add("Загалом вправ: ${progress.totalExercises}")

            val skillLevels = progress.skillLevels
            if (skillLevels.isNotEmpty()) {
                val skillsText = skillLevels.entries
                    .take(4)
                    .joinToString(", ") { (skill, level) ->
                        "${skill.getDisplayName()}: $level/100"
                    }
                parts.add("Рівень навичок: $skillsText")
            }
        }

        if (weakestSkills.isNotEmpty()) {
            val weakest = weakestSkills.take(3).joinToString(", ") { (skill, level) ->
                "$skill ($level/100)"
            }
            parts.add("Слабкі місця: $weakest")
        }

        diagnosticResult?.let { result ->
            parts.add("Початковий рівень (діагностика): ${result.calculateOverallLevel()}/100")
        }

        if (recentActivity.isNotBlank()) {
            parts.add("Недавня активність: $recentActivity")
        }

        return parts.joinToString("\n")
    }

    private fun formatGoal(goal: UserGoal): String {
        return goal.getDisplayName()
    }

    companion object {
        /**
         * Creates empty context for new users
         */
        fun empty(): ConversationContext {
            return ConversationContext(
                userProfile = null,
                userProgress = null,
                diagnosticResult = null,
                weakestSkills = emptyList(),
                recentActivity = "Користувач щойно почав"
            )
        }
    }
}

package com.aivoicepower.ui.screens.progress

import com.aivoicepower.domain.model.user.SkillType

data class SkillDetailState(
    val isLoading: Boolean = true,
    val skillType: SkillType = SkillType.DICTION,
    val currentLevel: Int = 0,
    val initialLevel: Int = 0,
    val historyPoints: List<SkillHistoryPoint> = emptyList(),
    val impactfulExercises: List<ExerciseImpact> = emptyList(),
    val recommendations: List<SkillRecommendation> = emptyList(),
    val totalPracticeMinutes: Int = 0,
    val error: String? = null
)

data class SkillHistoryPoint(
    val date: String,
    val level: Int
)

data class ExerciseImpact(
    val exerciseName: String,
    val completionCount: Int,
    val impactScore: Int // 0-100
)

data class SkillRecommendation(
    val tip: String,
    val exerciseName: String? = null,
    val courseId: String? = null,
    val lessonId: String? = null
)

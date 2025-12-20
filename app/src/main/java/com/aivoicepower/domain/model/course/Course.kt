package com.aivoicepower.domain.model.course

import com.aivoicepower.domain.model.user.SkillType

// New domain model from Phase 0.5 specification
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val lessons: List<NewLesson>,
    val isPremium: Boolean,
    val estimatedDays: Int,
    val difficulty: Difficulty,
    val skills: List<SkillType> // Навички, які розвиває курс
) {
    val totalLessons: Int
        get() = lessons.size

    val estimatedMinutes: Int
        get() = lessons.sumOf { it.estimatedMinutes }
}

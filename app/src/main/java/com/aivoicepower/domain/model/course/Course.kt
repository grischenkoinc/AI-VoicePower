package com.aivoicepower.domain.model.course

import com.aivoicepower.domain.model.user.SkillType

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,           // Emoji замість iconRes для простоти
    val totalLessons: Int,
    val isPremium: Boolean,          // Чи потрібен Premium для уроків 8+
    val estimatedDays: Int,
    val difficulty: Difficulty,
    val skills: List<SkillType>,     // Які навички розвиває
    val lessons: List<Lesson>        // Перші 7 уроків завантажені
)

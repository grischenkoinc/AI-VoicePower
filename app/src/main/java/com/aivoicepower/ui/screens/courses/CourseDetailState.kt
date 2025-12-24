package com.aivoicepower.ui.screens.courses

import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Lesson

data class CourseDetailState(
    val course: Course? = null,
    val lessonsWithProgress: List<LessonWithProgress> = emptyList(),
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,
    val progressPercent: Int = 0,
    val isPremium: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class LessonWithProgress(
    val lesson: Lesson,
    val isCompleted: Boolean,
    val isLocked: Boolean,  // Урок 8+ потребує Premium
    val weekNumber: Int     // 1, 2, 3 для групування
)

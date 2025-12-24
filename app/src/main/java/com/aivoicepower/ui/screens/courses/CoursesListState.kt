package com.aivoicepower.ui.screens.courses

import com.aivoicepower.domain.model.course.Course

data class CoursesListState(
    val courses: List<CourseWithProgress> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

data class CourseWithProgress(
    val course: Course,
    val completedLessons: Int,
    val totalLessons: Int,
    val progressPercent: Int
) {
    val isStarted: Boolean = completedLessons > 0
    val isCompleted: Boolean = completedLessons >= totalLessons
}

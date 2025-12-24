package com.aivoicepower.ui.screens.courses

sealed class CoursesListEvent {
    data class SearchQueryChanged(val query: String) : CoursesListEvent()
    data class CourseClicked(val courseId: String) : CoursesListEvent()
    object Refresh : CoursesListEvent()
}

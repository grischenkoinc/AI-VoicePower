package com.aivoicepower.ui.screens.courses

sealed class CourseDetailEvent {
    data class LessonClicked(val lessonId: String) : CourseDetailEvent()
    object UpgradeToPremiumClicked : CourseDetailEvent()
    object Refresh : CourseDetailEvent()
}

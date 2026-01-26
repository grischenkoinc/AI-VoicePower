package com.aivoicepower.domain.model.home

data class CurrentCourse(
    val courseId: String,
    val courseName: String,
    val nextLessonNumber: Int,
    val nextLessonId: String, // The actual lessonId (e.g., "lesson_1", "intonation_lesson_1", etc.)
    val totalLessons: Int,
    val color: String, // Hex color
    val icon: String, // Emoji
    val navigationRoute: String
)

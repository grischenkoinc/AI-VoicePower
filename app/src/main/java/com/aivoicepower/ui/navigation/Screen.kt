package com.aivoicepower.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Diagnostic : Screen("diagnostic")
    object DiagnosticResult : Screen("diagnostic_result")
    object Home : Screen("home")
    object Courses : Screen("courses")
    object CourseDetail : Screen("course/{courseId}") {
        fun createRoute(courseId: String) = "course/$courseId"
    }
    object Lesson : Screen("lesson/{courseId}/{lessonId}") {
        fun createRoute(courseId: String, lessonId: String) = "lesson/$courseId/$lessonId"
    }
    object Results : Screen("results/{recordingId}") {
        fun createRoute(recordingId: String) = "results/$recordingId"
    }
    object Premium : Screen("premium")
}

package com.aivoicepower.ui.navigation

sealed class NavRoutes(val route: String) {
    // Main screens
    object Home : NavRoutes("home")
    object Onboarding : NavRoutes("onboarding")
    object Diagnostic : NavRoutes("diagnostic")
    object DiagnosticResult : NavRoutes("diagnostic_result")

    // Warmup
    object Warmup : NavRoutes("warmup")
    object QuickWarmup : NavRoutes("warmup/quick")
    object FullWarmup : NavRoutes("warmup/full")

    // Courses
    object Courses : NavRoutes("courses")
    object CourseDetail : NavRoutes("courses/{courseId}") {
        fun createRoute(courseId: String) = "courses/$courseId"
    }
    object Lesson : NavRoutes("courses/{courseId}/lessons/{lessonId}") {
        fun createRoute(courseId: String, lessonId: String) = "courses/$courseId/lessons/$lessonId"
    }

    // Improvisation
    object Improvisation : NavRoutes("improvisation")
    object RandomTopic : NavRoutes("improvisation/random_topic")
    object ChallengeMode : NavRoutes("improvisation/challenge")

    // AI Coach
    object AiCoach : NavRoutes("ai_coach")

    // Progress
    object Progress : NavRoutes("progress")
    object ProgressDetail : NavRoutes("progress/detail")

    // Settings
    object Settings : NavRoutes("settings")

    // Results
    object Results : NavRoutes("results/{lessonId}/{exerciseId}") {
        fun createRoute(lessonId: String, exerciseId: String) = "results/$lessonId/$exerciseId"
    }
}

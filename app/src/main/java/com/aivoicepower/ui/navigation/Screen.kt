package com.aivoicepower.ui.navigation

sealed class Screen(val route: String) {
    // Onboarding flow
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Diagnostic : Screen("diagnostic")
    object DiagnosticResult : Screen("diagnostic_result")

    // Main screens (Bottom Navigation)
    object Main : Screen("main")
    object Home : Screen("home")
    object Courses : Screen("courses")
    object Progress : Screen("progress")

    // Course details
    object CourseDetail : Screen("course/{courseId}") {
        fun createRoute(courseId: String) = "course/$courseId"
    }
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
    object Results : Screen("results/{lessonId}/{exerciseId}") {
        fun createRoute(lessonId: String, exerciseId: String) = "results/$lessonId/$exerciseId"
    }

    // Warmup screens
    object Warmup : Screen("warmup")
    object WarmupArticulation : Screen("warmup/articulation")
    object WarmupBreathing : Screen("warmup/breathing")
    object WarmupVoice : Screen("warmup/voice")
    object WarmupQuick : Screen("warmup/quick")

    // Improvisation screens (Phase 5)
    object Improvisation : Screen("improvisation")
    object RandomTopic : Screen("improvisation/random-topic")
    object Storytelling : Screen("improvisation/storytelling")
    object DailyChallenge : Screen("improvisation/daily-challenge")
    object Debate : Screen("improvisation/debate")
    object SalesPitch : Screen("improvisation/sales-pitch")

    // AI Coach (Phase 6)
    object AiCoach : Screen("ai-coach")

    // Premium
    object Premium : Screen("premium")

    // Progress sub-screens
    object Achievements : Screen("progress/achievements")
    object Compare : Screen("progress/compare")
    object RecordingHistory : Screen("progress/recording-history")
}

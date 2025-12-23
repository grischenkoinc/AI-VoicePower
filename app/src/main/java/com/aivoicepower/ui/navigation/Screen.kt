package com.aivoicepower.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
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
}

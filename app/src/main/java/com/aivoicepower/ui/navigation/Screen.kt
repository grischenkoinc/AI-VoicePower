package com.aivoicepower.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Diagnostic : Screen("diagnostic")
    object DiagnosticResult : Screen("diagnostic_result")
    object Home : Screen("home")
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
    object Results : Screen("results/{lessonId}/{exerciseId}") {
        fun createRoute(lessonId: String, exerciseId: String) = "results/$lessonId/$exerciseId"
    }
}

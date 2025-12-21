package com.aivoicepower.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aivoicepower.ui.screens.home.HomeScreen
import com.aivoicepower.ui.screens.courses.LessonScreen
import com.aivoicepower.ui.screens.results.ResultsScreen
import com.aivoicepower.ui.screens.onboarding.OnboardingScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticResultScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToDiagnostic = {
                    navController.navigate(Screen.Diagnostic.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Diagnostic.route) {
            DiagnosticScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.DiagnosticResult.route) {
                        popUpTo(Screen.Diagnostic.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.DiagnosticResult.route) {
            DiagnosticResultScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.DiagnosticResult.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToCourse = { courseId ->
                    // TODO: Navigate to course detail when implemented
                },
                onNavigateToAiCoach = {
                    // TODO: Navigate to AI Coach when implemented
                },
                onNavigateToLesson = { courseId, lessonId ->
                    // TODO: Navigate to specific lesson when courses are implemented
                    navController.navigate(Screen.Lesson.createRoute(lessonId))
                },
                onNavigateToWarmup = {
                    // TODO: Navigate to warmup when implemented
                },
                onNavigateToCourses = {
                    // TODO: Navigate to courses list when implemented
                },
                onNavigateToImprovisation = {
                    // TODO: Navigate to improvisation when implemented
                },
                onNavigateToProgress = {
                    // TODO: Navigate to progress when implemented
                },
                onNavigateToQuickWarmup = {
                    // TODO: Navigate to quick warmup when implemented
                },
                onNavigateToRandomTopic = {
                    // TODO: Navigate to random topic when implemented
                }
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            LessonScreen(
                lessonId = lessonId,
                onNavigateBack = { navController.popBackStack() },
                onExerciseComplete = { exerciseId ->
                    navController.navigate(Screen.Results.createRoute(lessonId, exerciseId))
                }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("exerciseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: return@composable
            ResultsScreen(
                lessonId = lessonId,
                exerciseId = exerciseId,
                onNavigateBack = { navController.popBackStack() },
                onNextExercise = { nextExerciseId ->
                    navController.navigate(Screen.Results.createRoute(lessonId, nextExerciseId)) {
                        popUpTo(Screen.Results.createRoute(lessonId, exerciseId)) { inclusive = true }
                    }
                }
            )
        }
    }
}

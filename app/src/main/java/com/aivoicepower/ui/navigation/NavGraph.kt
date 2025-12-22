package com.aivoicepower.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aivoicepower.ui.screens.home.HomeScreen
import com.aivoicepower.ui.screens.courses.CoursesListScreen
import com.aivoicepower.ui.screens.courses.CourseDetailScreen
import com.aivoicepower.ui.screens.courses.LessonScreen
import com.aivoicepower.ui.screens.results.ResultsScreen
import com.aivoicepower.ui.screens.onboarding.OnboardingScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticResultScreen
import com.aivoicepower.ui.screens.progress.ProgressScreen
import com.aivoicepower.ui.screens.progress.CompareScreen
import com.aivoicepower.ui.screens.progress.AchievementsScreen
import com.aivoicepower.ui.screens.progress.RecordingHistoryScreen
import com.aivoicepower.ui.screens.premium.PaywallScreen
import com.aivoicepower.ui.screens.premium.PaywallSource

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
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                },
                onNavigateToAiCoach = {
                    // TODO: Navigate to AI Coach when implemented
                },
                onNavigateToLesson = { courseId, lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(courseId, lessonId))
                },
                onNavigateToWarmup = {
                    // TODO: Navigate to warmup when implemented
                },
                onNavigateToCourses = {
                    navController.navigate(Screen.Courses.route)
                },
                onNavigateToImprovisation = {
                    // TODO: Navigate to improvisation when implemented
                },
                onNavigateToProgress = {
                    navController.navigate(Screen.Progress.route)
                },
                onNavigateToQuickWarmup = {
                    // TODO: Navigate to quick warmup when implemented
                },
                onNavigateToRandomTopic = {
                    // TODO: Navigate to random topic when implemented
                }
            )
        }

        composable(route = Screen.Courses.route) {
            CoursesListScreen(
                onNavigateToCourse = { courseId ->
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                }
            )
        }

        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            CourseDetailScreen(
                courseId = courseId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLesson = { course, lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(course, lessonId))
                },
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
                }
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            LessonScreen(
                courseId = courseId,
                lessonId = lessonId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(
                navArgument("recordingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val recordingId = backStackEntry.arguments?.getString("recordingId") ?: return@composable
            ResultsScreen(
                recordingId = recordingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Progress screens
        composable(route = Screen.Progress.route) {
            ProgressScreen(
                onNavigateToCompare = {
                    navController.navigate(Screen.Compare.route)
                },
                onNavigateToAchievements = {
                    navController.navigate(Screen.Achievements.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.RecordingHistory.route)
                }
            )
        }

        composable(route = Screen.Compare.route) {
            CompareScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Achievements.route) {
            AchievementsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.RecordingHistory.route) {
            RecordingHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResults = { recordingId ->
                    navController.navigate(Screen.Results.createRoute(recordingId))
                }
            )
        }

        // Premium/Paywall screen
        composable(route = Screen.Premium.route) {
            PaywallScreen(
                source = PaywallSource.UNKNOWN,
                onNavigateBack = { navController.popBackStack() },
                onPurchaseSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}

package com.aivoicepower.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aivoicepower.ui.screens.home.HomeScreen
import com.aivoicepower.ui.screens.improvisation.*
import com.aivoicepower.ui.screens.lesson.LessonScreen
import com.aivoicepower.ui.screens.results.ResultsScreen
import com.aivoicepower.ui.screens.warmup.*

/**
 * Navigation graph for Main screen (inside bottom navigation)
 *
 * TODO: Add Courses, Progress screens when repositories are ready
 */
@Composable
fun MainNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    onNavigateToAiCoach: () -> Unit,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // ===== HOME =====
        composable(route = Screen.Home.route) {
            HomeScreen(
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(lessonId))
                }
            )
        }

        // ===== COURSES (Placeholder) =====
        composable(route = Screen.Courses.route) {
            PlaceholderScreen("Курси - TODO (Phase 3-4)")
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

        // ===== WARMUP =====
        composable(route = Screen.Warmup.route) {
            WarmupScreen(
                onNavigateToArticulation = {
                    navController.navigate(Screen.WarmupArticulation.route)
                },
                onNavigateToBreathing = {
                    navController.navigate(Screen.WarmupBreathing.route)
                },
                onNavigateToVoice = {
                    navController.navigate(Screen.WarmupVoice.route)
                },
                onNavigateToQuick = {
                    navController.navigate(Screen.WarmupQuick.route)
                }
            )
        }

        composable(route = Screen.WarmupArticulation.route) {
            ArticulationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.WarmupBreathing.route) {
            BreathingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.WarmupVoice.route) {
            VoiceWarmupScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.WarmupQuick.route) {
            QuickWarmupScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ===== IMPROVISATION =====
        composable(route = Screen.Improvisation.route) {
            ImprovisationScreen(
                onNavigateToRandomTopic = {
                    navController.navigate(Screen.RandomTopic.route)
                },
                onNavigateToStorytelling = {
                    navController.navigate(Screen.Storytelling.route)
                },
                onNavigateToDebate = {
                    navController.navigate(Screen.Debate.route)
                },
                onNavigateToSales = {
                    navController.navigate(Screen.SalesPitch.route)
                },
                onNavigateToChallenge = {
                    navController.navigate(Screen.DailyChallenge.route)
                },
                onNavigateToPremium = onNavigateToPremium
            )
        }

        composable(route = Screen.RandomTopic.route) {
            RandomTopicScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResults = { recordingId ->
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Storytelling.route) {
            StorytellingScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResults = { recordingId ->
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.DailyChallenge.route) {
            DailyChallengeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResults = { recordingId ->
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Debate.route) {
            DebateScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.SalesPitch.route) {
            SalesPitchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ===== PROGRESS (Placeholder) =====
        composable(route = Screen.Progress.route) {
            PlaceholderScreen("Прогрес - TODO (Phase 7)")
        }
    }
}

@Composable
private fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

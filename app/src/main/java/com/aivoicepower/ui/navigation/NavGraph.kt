package com.aivoicepower.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aivoicepower.ui.screens.home.HomeScreen
import com.aivoicepower.ui.screens.lesson.LessonScreen
import com.aivoicepower.ui.screens.results.ResultsScreen
import com.aivoicepower.ui.screens.warmup.ArticulationScreen
import com.aivoicepower.ui.screens.warmup.BreathingScreen
import com.aivoicepower.ui.screens.warmup.WarmupScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(lessonId))
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

        // Warmup screens
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

        // Phase 2.2 - Articulation screen
        composable(route = Screen.WarmupArticulation.route) {
            ArticulationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Phase 2.3 - Breathing screen
        composable(route = Screen.WarmupBreathing.route) {
            BreathingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.WarmupVoice.route) {
            PlaceholderScreen(title = "Розминка голосу", onBack = { navController.popBackStack() })
        }

        composable(route = Screen.WarmupQuick.route) {
            PlaceholderScreen(title = "Швидка розминка", onBack = { navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Coming in Phase 2.2-2.5")
        }
    }
}

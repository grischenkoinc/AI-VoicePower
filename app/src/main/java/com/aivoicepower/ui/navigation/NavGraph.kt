package com.aivoicepower.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aivoicepower.ui.screens.home.HomeScreen
import com.aivoicepower.ui.screens.lesson.LessonScreen
import com.aivoicepower.ui.screens.results.ResultsScreen

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
    }
}

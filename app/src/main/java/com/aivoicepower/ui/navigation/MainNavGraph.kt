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
import com.aivoicepower.ui.screens.courses.CourseDetailScreen
import com.aivoicepower.ui.screens.courses.CoursesListScreen
import com.aivoicepower.ui.screens.courses.CoursesScreen
import com.aivoicepower.ui.screens.home.HomeScreen
import com.aivoicepower.ui.screens.improvisation.*
import com.aivoicepower.ui.screens.courses.LessonScreen
import com.aivoicepower.ui.screens.progress.AchievementsScreen
import com.aivoicepower.ui.screens.progress.CompareScreen
import com.aivoicepower.ui.screens.progress.ProgressScreen
import com.aivoicepower.ui.screens.progress.RecordingHistoryScreen
import com.aivoicepower.ui.screens.progress.SkillDetailScreen
import com.aivoicepower.ui.screens.results.ResultsScreen
import com.aivoicepower.ui.screens.premium.PaywallScreen
import com.aivoicepower.ui.screens.settings.SettingsScreen
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
                onNavigateToCourse = { courseId ->
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                },
                onNavigateToLesson = { courseId, lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(courseId, lessonId))
                },
                onNavigateToImprovisation = {
                    navController.navigate(Screen.Improvisation.route)
                },
                onNavigateToAICoach = onNavigateToAiCoach,
                onNavigateToWarmup = {
                    navController.navigate(Screen.Warmup.route)
                },
                onNavigateToRecord = {
                    // TODO: Navigate to record screen
                },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.Progress.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // ===== COURSES =====
        composable(route = Screen.Courses.route) {
            CoursesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCourse = { courseId ->
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                }
            )
        }

        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            CourseDetailScreen(
                courseId = courseId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLesson = { courseId, lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(courseId, lessonId))
                },
                onNavigateToPremium = onNavigateToPremium
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonScreen(
                courseId = courseId,
                lessonId = lessonId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNextLesson = { nextCourseId, nextLessonId ->
                    navController.navigate(Screen.Lesson.createRoute(nextCourseId, nextLessonId)) {
                        popUpTo(Screen.Lesson.createRoute(courseId, lessonId)) { inclusive = true }
                    }
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

        // ===== PROGRESS =====
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
                },
                onNavigateToSkillDetail = { skillType ->
                    navController.navigate(Screen.SkillDetail.createRoute(skillType.name))
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
                    // TODO: Navigate to recording results screen
                }
            )
        }

        composable(
            route = Screen.SkillDetail.route,
            arguments = listOf(navArgument("skillType") { type = NavType.StringType })
        ) { backStackEntry ->
            val skillType = backStackEntry.arguments?.getString("skillType") ?: return@composable
            SkillDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ===== SETTINGS =====
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) }
            )
        }

        // ===== PREMIUM/PAYWALL =====
        composable(route = Screen.Premium.route) {
            PaywallScreen(
                onNavigateBack = { navController.popBackStack() },
                onPurchaseSuccess = { navController.popBackStack() }
            )
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

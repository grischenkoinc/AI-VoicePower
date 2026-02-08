package com.aivoicepower.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aivoicepower.ui.navigation.MainNavGraph
import com.aivoicepower.ui.navigation.Screen
import com.aivoicepower.ui.screens.progress.CelebrationOverlay
import com.aivoicepower.ui.screens.progress.CelebrationViewModel

/**
 * Main screen with Bottom Navigation and FAB for AI Coach
 */
@Composable
fun MainScreen(
    onNavigateToAiCoach: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    rootNavController: NavHostController = rememberNavController()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val celebrationViewModel: CelebrationViewModel = hiltViewModel()
    val currentCelebration by celebrationViewModel.currentCelebration.collectAsStateWithLifecycle()

    // Define bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Courses,
        BottomNavItem.Warmup,
        BottomNavItem.Improvisation,
        BottomNavItem.Progress
    )

    // Check if current screen should show bottom bar
    val shouldShowBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1F2937),
                    tonalElevation = 0.dp, // ВАЖЛИВО: без elevation
                    modifier = Modifier.fillMaxWidth()
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    tint = if (selected) item.selectedColor else Color(0xFF9CA3AF)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    color = if (selected) Color(0xFF1F2937) else Color(0xFF9CA3AF)
                                )
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = item.selectedColor,
                                unselectedIconColor = Color(0xFF9CA3AF),
                                selectedTextColor = Color(0xFF1F2937),
                                unselectedTextColor = Color(0xFF9CA3AF),
                                indicatorColor = Color.Transparent // БЕЗ індикатора
                            ),
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (shouldShowBottomBar) {
                FloatingActionButton(
                    onClick = onNavigateToAiCoach,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Тренер"
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MainNavGraph(
                navController = navController,
                rootNavController = rootNavController,
                onNavigateToAiCoach = onNavigateToAiCoach,
                onNavigateToPremium = onNavigateToPremium,
                modifier = Modifier
            )

            // Celebration overlay
            currentCelebration?.let { achievement ->
                CelebrationOverlay(
                    achievement = achievement,
                    onDismiss = { celebrationViewModel.dismiss() }
                )
            }
        }
    }
}

/**
 * Bottom navigation items
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val selectedColor: Color
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        label = "Головна",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        selectedColor = Color(0xFF667EEA)
    )

    object Courses : BottomNavItem(
        route = Screen.Courses.route,
        label = "Курси",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School,
        selectedColor = Color(0xFF8B5CF6)
    )

    object Warmup : BottomNavItem(
        route = Screen.Warmup.route,
        label = "Розминка",
        selectedIcon = Icons.Filled.FitnessCenter,
        unselectedIcon = Icons.Outlined.FitnessCenter,
        selectedColor = Color(0xFFEC4899)
    )

    object Improvisation : BottomNavItem(
        route = Screen.Improvisation.route,
        label = "Improv",
        selectedIcon = Icons.Filled.Lightbulb,
        unselectedIcon = Icons.Outlined.Lightbulb,
        selectedColor = Color(0xFFF59E0B)
    )

    object Progress : BottomNavItem(
        route = Screen.Progress.route,
        label = "Прогрес",
        selectedIcon = Icons.Filled.TrendingUp,
        unselectedIcon = Icons.Outlined.TrendingUp,
        selectedColor = Color(0xFF10B981)
    )
}

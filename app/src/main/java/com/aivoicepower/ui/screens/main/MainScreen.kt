package com.aivoicepower.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aivoicepower.ui.navigation.MainNavGraph
import com.aivoicepower.ui.navigation.Screen

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
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
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
    ) { paddingValues ->
        MainNavGraph(
            navController = navController,
            rootNavController = rootNavController,
            onNavigateToAiCoach = onNavigateToAiCoach,
            onNavigateToPremium = onNavigateToPremium,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Bottom navigation items
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        label = "Головна",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Filled.Home
    )

    object Courses : BottomNavItem(
        route = Screen.Courses.route,
        label = "Курси",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Filled.School
    )

    object Warmup : BottomNavItem(
        route = Screen.Warmup.route,
        label = "Розминка",
        selectedIcon = Icons.Filled.FitnessCenter,
        unselectedIcon = Icons.Filled.FitnessCenter
    )

    object Improvisation : BottomNavItem(
        route = Screen.Improvisation.route,
        label = "Імпровізація",
        selectedIcon = Icons.Filled.Lightbulb,
        unselectedIcon = Icons.Filled.Lightbulb
    )

    object Progress : BottomNavItem(
        route = Screen.Progress.route,
        label = "Прогрес",
        selectedIcon = Icons.Filled.TrendingUp,
        unselectedIcon = Icons.Filled.TrendingUp
    )
}

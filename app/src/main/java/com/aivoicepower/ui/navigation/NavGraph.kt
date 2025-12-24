package com.aivoicepower.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aivoicepower.ui.screens.aicoach.AiCoachScreen
import com.aivoicepower.ui.screens.main.MainScreen

/**
 * Root navigation graph for the app
 *
 * TODO: Add Onboarding, Diagnostic when repositories are implemented
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Main.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ===== MAIN SCREEN (with Bottom Navigation) =====

        composable(route = Screen.Main.route) {
            MainScreen(
                onNavigateToAiCoach = {
                    navController.navigate(Screen.AiCoach.route)
                },
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
                },
                rootNavController = navController
            )
        }

        // ===== AI COACH =====

        composable(route = Screen.AiCoach.route) {
            AiCoachScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
                }
            )
        }

        // ===== PREMIUM =====

        composable(route = Screen.Premium.route) {
            PremiumPlaceholderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Placeholder for Premium screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumPlaceholderScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            Text("Premium Screen - TODO")
        }
    }
}

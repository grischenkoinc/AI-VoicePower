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
import com.aivoicepower.ui.screens.diagnostic.DiagnosticAnalysisScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticResult
import com.aivoicepower.ui.screens.diagnostic.DiagnosticResultScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticScreen
import com.aivoicepower.ui.screens.main.MainScreen
import com.aivoicepower.ui.screens.onboarding.OnboardingScreen
import com.aivoicepower.ui.screens.onboarding.SplashScreen

/**
 * Temporary data holder for diagnostic flow
 * TODO: Replace with proper state management (ViewModel or SavedStateHandle)
 */
object DiagnosticDataHolder {
    var recordingPaths: List<String> = emptyList()
    var result: DiagnosticResult? = null
}

/**
 * Root navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ===== ONBOARDING FLOW =====

        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

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
                onComplete = { recordingPaths ->
                    // Store recording paths temporarily
                    DiagnosticDataHolder.recordingPaths = recordingPaths
                    // Navigate to analysis screen
                    navController.navigate(Screen.DiagnosticAnalysis.route) {
                        popUpTo(Screen.Diagnostic.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.DiagnosticAnalysis.route) {
            DiagnosticAnalysisScreen(
                recordingPaths = DiagnosticDataHolder.recordingPaths,
                onAnalysisComplete = { result ->
                    // Store result for DiagnosticResultScreen
                    DiagnosticDataHolder.result = result
                    navController.navigate(Screen.DiagnosticResult.route) {
                        popUpTo(Screen.DiagnosticAnalysis.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.DiagnosticResult.route) {
            DiagnosticResultScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.DiagnosticResult.route) { inclusive = true }
                    }
                }
            )
        }

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

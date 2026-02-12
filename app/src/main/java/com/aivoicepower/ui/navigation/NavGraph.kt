package com.aivoicepower.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.data.firebase.auth.GoogleSignInHelper
import com.aivoicepower.ui.screens.aicoach.AiCoachScreen
import com.aivoicepower.ui.screens.auth.AuthScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticAnalysisScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticResult
import com.aivoicepower.ui.screens.diagnostic.DiagnosticResultScreen
import com.aivoicepower.ui.screens.diagnostic.DiagnosticScreen
import com.aivoicepower.ui.screens.main.MainScreen
import com.aivoicepower.ui.screens.onboarding.OnboardingScreen
import com.aivoicepower.ui.screens.onboarding.SplashScreen
import com.aivoicepower.ui.screens.premium.PaywallScreen
import com.aivoicepower.ui.screens.username.UserNameScreen

/**
 * Temporary data holder for diagnostic flow
 * TODO: Replace with proper state management (ViewModel or SavedStateHandle)
 */
object DiagnosticDataHolder {
    var recordingPaths: List<String> = emptyList()
    var expectedTexts: List<String?> = emptyList()
    var result: DiagnosticResult? = null
}

/**
 * Root navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    googleSignInHelper: GoogleSignInHelper,
    rewardedAdManager: RewardedAdManager? = null,
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

        composable(
            route = "onboarding?startPage={startPage}",
            arguments = listOf(navArgument("startPage") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val startPage = backStackEntry.arguments?.getInt("startPage") ?: 0
            OnboardingScreen(
                startPage = startPage,
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.createRoute("onboarding"))
                },
                onNavigateToDiagnostic = {
                    navController.navigate(Screen.Diagnostic.route) {
                        popUpTo("onboarding?startPage={startPage}") { inclusive = true }
                    }
                }
            )
        }

        // ===== AUTH SCREEN =====

        composable(
            route = "auth?source={source}",
            arguments = listOf(navArgument("source") {
                type = NavType.StringType
                defaultValue = "onboarding"
            })
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "onboarding"
            val isFromSettings = source == "settings"

            AuthScreen(
                onAuthSuccess = {
                    if (isFromSettings) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.UserName.route) {
                            popUpTo("auth?source={source}") { inclusive = true }
                        }
                    }
                },
                onSkip = {
                    navController.navigate(Screen.UserName.route) {
                        popUpTo("auth?source={source}") { inclusive = true }
                    }
                },
                googleSignInHelper = googleSignInHelper,
                showSkipButton = !isFromSettings
            )
        }

        // ===== USER NAME SCREEN =====

        composable(route = Screen.UserName.route) {
            UserNameScreen(
                onContinue = {
                    navController.navigate("onboarding?startPage=1") {
                        popUpTo(Screen.UserName.route) { inclusive = true }
                    }
                }
            )
        }

        // ===== DIAGNOSTIC FLOW =====

        composable(route = Screen.Diagnostic.route) {
            DiagnosticScreen(
                onComplete = { recordingPaths, expectedTexts ->
                    DiagnosticDataHolder.recordingPaths = recordingPaths
                    DiagnosticDataHolder.expectedTexts = expectedTexts
                    navController.navigate(Screen.DiagnosticAnalysis.route) {
                        popUpTo(Screen.Diagnostic.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.DiagnosticAnalysis.route) {
            DiagnosticAnalysisScreen(
                recordingPaths = DiagnosticDataHolder.recordingPaths,
                expectedTexts = DiagnosticDataHolder.expectedTexts,
                onAnalysisComplete = { result ->
                    DiagnosticDataHolder.result = result
                    navController.navigate(Screen.DiagnosticResult.route) {
                        popUpTo(Screen.DiagnosticAnalysis.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.DiagnosticResult.route) {
            DiagnosticResultScreen(
                onContinue = {
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
                rewardedAdManager = rewardedAdManager,
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
            PaywallScreen(
                onNavigateBack = { navController.popBackStack() },
                onPurchaseSuccess = { navController.popBackStack() }
            )
        }

    }
}

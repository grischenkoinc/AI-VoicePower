package com.aivoicepower.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState(initial = null)

    // Pulse animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(hasCompletedOnboarding) {
        delay(1500)

        when (hasCompletedOnboarding) {
            true -> onNavigateToHome()
            false -> onNavigateToOnboarding()
            null -> { /* Still loading */ }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with pulse animation
            Text(
                text = "🎤",
                fontSize = 80.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "AI VoicePower",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Твій голос — твоя сила",
                style = AppTypography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (hasCompletedOnboarding == null) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

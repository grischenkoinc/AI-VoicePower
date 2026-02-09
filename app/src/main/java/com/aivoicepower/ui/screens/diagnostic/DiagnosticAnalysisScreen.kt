package com.aivoicepower.ui.screens.diagnostic

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*
import kotlinx.coroutines.delay

@Composable
fun DiagnosticAnalysisScreen(
    recordingPaths: List<String>,
    expectedTexts: List<String?> = emptyList(),
    onAnalysisComplete: (DiagnosticResult) -> Unit,
    viewModel: DiagnosticViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val steps = listOf("Читання", "Дикція", "Емоційність", "Вільна мова")

    LaunchedEffect(Unit) {
        // Simulate analysis steps
        for (i in steps.indices) {
            delay(2000) // 2 seconds per step
            currentStep = i + 1
        }

        // Start real AI analysis
        viewModel.analyzeDiagnostic(
            recordingPaths = recordingPaths,
            expectedTexts = expectedTexts,
            onSuccess = { result ->
                onAnalysisComplete(result)
            },
            onError = { _ ->
                // Handle error - можна показати повідомлення
                onAnalysisComplete(DiagnosticResult.mock()) // Fallback to mock
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // AI Icon with pulse
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .pulseAnimation(scaleFrom = 1f, scaleTo = 1.1f)
                    .shadow(
                        elevation = 24.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFF667EEA).copy(alpha = 0.5f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✨", fontSize = 56.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = "Аналізую ваше мовлення...",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AI-коуч вивчає ваші записи",
                style = AppTypography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Progress steps
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                steps.forEachIndexed { index, step ->
                    AnalysisStep(
                        step = step,
                        isCompleted = index < currentStep,
                        isActive = index == currentStep
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisStep(
    step: String,
    isCompleted: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isActive) 16.dp else 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isActive)
                    Color(0xFF667EEA).copy(alpha = 0.3f)
                else
                    Color.Black.copy(alpha = 0.1f)
            )
            .background(
                if (isActive)
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.9f)
                        )
                    )
                else
                    Brush.linearGradient(colors = listOf(Color.White, Color.White)),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    when {
                        isCompleted -> Color(0xFF22C55E)
                        isActive -> Color(0xFF667EEA)
                        else -> Color(0xFFE5E7EB)
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    isCompleted -> "✓"
                    isActive -> "..."
                    else -> ""
                },
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Step name
        Text(
            text = step,
            style = AppTypography.bodyMedium,
            color = if (isActive) Color(0xFF667EEA) else TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        // Loading indicator for active step
        if (isActive) {
            LoadingDots()
        }
    }
}

@Composable
private fun LoadingDots(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "dot_$index")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        Color(0xFF667EEA).copy(alpha = alpha),
                        CircleShape
                    )
            )
        }
    }
}

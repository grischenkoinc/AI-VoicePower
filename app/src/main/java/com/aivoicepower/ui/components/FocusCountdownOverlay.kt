package com.aivoicepower.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import kotlinx.coroutines.delay

/**
 * Full-screen focus overlay shown before exercises.
 * Displays a breathing orb, exercise info, and a 3-2-1 countdown.
 *
 * @param exerciseName Name of the exercise (e.g. "Дебати", "Вільна тема")
 * @param topic Optional topic/description
 * @param countdownSeconds Countdown duration (default 3)
 * @param onComplete Called when countdown finishes or user taps to skip
 */
@Composable
fun FocusCountdownOverlay(
    exerciseName: String,
    topic: String = "",
    countdownSeconds: Int = 3,
    onComplete: () -> Unit
) {
    var currentCount by remember { mutableIntStateOf(countdownSeconds) }
    var showBreathText by remember { mutableStateOf(true) }
    var countdownStarted by remember { mutableStateOf(false) }

    // Phase 1: Show breath text, then start countdown
    LaunchedEffect(Unit) {
        delay(1200)
        showBreathText = false
        countdownStarted = true
        for (i in countdownSeconds downTo 1) {
            currentCount = i
            delay(1000)
        }
        onComplete()
    }

    // === BREATHING ORB ===
    val infiniteTransition = rememberInfiniteTransition(label = "focusBreathing")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onComplete() },
        contentAlignment = Alignment.Center
    ) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // === BREATHING ORB ===
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = orbScale
                        scaleY = orbScale
                    },
                contentAlignment = Alignment.Center
            ) {
                // Glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = CircleShape,
                            spotColor = Color(0xFF667EEA).copy(alpha = glowAlpha)
                        )
                )
                // Orb circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4ECDC4).copy(alpha = 0.7f),
                                    Color(0xFF667EEA).copy(alpha = 0.5f),
                                    Color(0xFF764BA2).copy(alpha = 0.3f)
                                )
                            ),
                            CircleShape
                        )
                )
                // Inner glow
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // === EXERCISE NAME ===
            Text(
                text = exerciseName,
                style = AppTypography.titleLarge,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (topic.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = topic,
                    style = AppTypography.bodyMedium,
                    color = TextColors.onDarkSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // === BREATH TEXT OR COUNTDOWN ===
            Box(
                modifier = Modifier.height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showBreathText,
                    enter = fadeIn(tween(400)),
                    exit = fadeOut(tween(300))
                ) {
                    Text(
                        text = "Зробiть глибокий вдих...",
                        style = AppTypography.titleMedium,
                        color = Color(0xFF4ECDC4),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = countdownStarted,
                    enter = fadeIn(tween(200))
                ) {
                    // Animated countdown number
                    key(currentCount) {
                        val enterScale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "enterScale_$currentCount"
                        )

                        Text(
                            text = "$currentCount",
                            style = AppTypography.displayLarge,
                            color = Color.White,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.graphicsLayer {
                                scaleX = enterScale
                                scaleY = enterScale
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // === SKIP HINT ===
            Text(
                text = "Торкнiться для пропуску",
                style = AppTypography.bodySmall,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

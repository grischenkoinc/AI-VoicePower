package com.aivoicepower.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.audio.LocalSoundManager
import com.aivoicepower.audio.SoundEffect
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import kotlinx.coroutines.delay

/**
 * Full-screen focus overlay shown before exercises/lessons.
 * White card with blue accent border, breathing orb, exercise info, 3-2-1 countdown.
 */
@Composable
fun FocusCountdownOverlay(
    exerciseName: String,
    topic: String = "",
    countdownSeconds: Int = 3,
    onComplete: () -> Unit
) {
    val soundManager = LocalSoundManager.current
    var currentCount by remember { mutableIntStateOf(countdownSeconds) }
    var showBreathText by remember { mutableStateOf(true) }
    var countdownStarted by remember { mutableStateOf(false) }

    var skipped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1200)
        if (skipped) return@LaunchedEffect
        showBreathText = false
        countdownStarted = true
        soundManager.play(SoundEffect.COUNTDOWN_TICK)
        for (i in countdownSeconds downTo 1) {
            if (skipped) return@LaunchedEffect
            currentCount = i
            delay(1000)
        }
        if (skipped) return@LaunchedEffect
        currentCount = 0
        soundManager.play(SoundEffect.COUNTDOWN_GO)
        delay(100)
        onComplete()
    }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.stopAll()
        }
    }

    // Breathing orb
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
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Card entry animation
    var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { cardVisible = true }
    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "cardAlpha"
    )
    val cardScale by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0.92f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "cardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { skipped = true; onComplete() },
        contentAlignment = Alignment.Center
    ) {
        GradientBackground(content = {})

        // White card with blue accent border — almost full screen
        Box(
            modifier = Modifier
                .graphicsLayer {
                    alpha = cardAlpha
                    scaleX = cardScale
                    scaleY = cardScale
                }
                .padding(16.dp)
                .fillMaxSize()
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color(0xFF667EEA).copy(alpha = 0.3f)
                )
                // Blue accent border
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(6.dp) // padding between border and content
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Breathing orb
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .graphicsLayer {
                            scaleX = orbScale
                            scaleY = orbScale
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .shadow(
                                elevation = 24.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFF667EEA).copy(alpha = glowAlpha)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF667EEA).copy(alpha = 0.6f),
                                        Color(0xFF764BA2).copy(alpha = 0.4f),
                                        Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                    )
                                ),
                                CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Exercise name
                Text(
                    text = exerciseName,
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                )

                if (topic.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = topic,
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Breath text or countdown
                Box(
                    modifier = Modifier.height(90.dp),
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
                            color = Color(0xFF667EEA),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = countdownStarted,
                        enter = fadeIn(tween(200))
                    ) {
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
                                color = Color(0xFF667EEA),
                                fontSize = 72.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = enterScale
                                    scaleY = enterScale
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Skip hint
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFF5F5F7),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Торкнiться для пропуску",
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

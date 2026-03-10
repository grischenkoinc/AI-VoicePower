package com.aivoicepower.ui.screens.onboarding

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicepower.R
import com.aivoicepower.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState(initial = null)
    val context = LocalContext.current

    // Sequenced phase triggers — start quickly since gradient bg is already visible
    var showEmoji by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showDivider by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }

    // Play sound in a separate coroutine — never block the animation sequence
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                MediaPlayer.create(context, R.raw.sound_splash_brand)?.apply {
                    setVolume(0.7f, 0.7f)
                    playbackParams = playbackParams.setSpeed(1.2f)
                    setOnCompletionListener { release() }
                    start()
                }
            } catch (_: Exception) {}
        }
    }

    // Animation sequence — runs independently, never waits for sound
    LaunchedEffect(Unit) {
        delay(50)
        showEmoji = true
        delay(700)
        showTitle = true
        delay(400)
        showDivider = true
        delay(350)
        showSubtitle = true
    }

    // === MICROPHONE: smooth scale with gentle overshoot ===
    val emojiScale by animateFloatAsState(
        targetValue = if (showEmoji) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 100f),
        label = "emojiScale"
    )

    // === TITLE ===
    val titleAlpha by animateFloatAsState(
        targetValue = if (showTitle) 1f else 0f,
        animationSpec = tween(800, easing = EaseOut),
        label = "titleAlpha"
    )
    val titleScale by animateFloatAsState(
        targetValue = if (showTitle) 1f else 1.08f,
        animationSpec = tween(900, easing = EaseOut),
        label = "titleScale"
    )

    // === DIVIDER ===
    val dividerProgress by animateFloatAsState(
        targetValue = if (showDivider) 1f else 0f,
        animationSpec = tween(800, easing = EaseOut),
        label = "dividerProgress"
    )

    // === SUBTITLE ===
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (showSubtitle) 1f else 0f,
        animationSpec = tween(700, easing = EaseOut),
        label = "subtitleAlpha"
    )
    val subtitleOffsetY by animateFloatAsState(
        targetValue = if (showSubtitle) 0f else 10f,
        animationSpec = tween(700, easing = EaseOut),
        label = "subtitleOffset"
    )

    // === CONTINUOUS AMBIENT ANIMATIONS (2 only — minimal for smooth performance) ===
    val infiniteTransition = rememberInfiniteTransition(label = "splashAmbient")

    // Single animation drives glow (scale+alpha) and float offset
    val ambientPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = EaseInOut), RepeatMode.Reverse
        ), label = "ambientPhase"
    )
    val glowScale = 0.92f + ambientPhase * 0.2f
    val glowAlpha = 0.18f + ambientPhase * 0.32f
    val floatOffset = ambientPhase * 7f

    // Ring rotation + shimmer from single animation
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(10000, easing = LinearEasing), RepeatMode.Restart
        ), label = "ringRotation"
    )
    // Fast shimmer: full sweep every ~1.7s (60° of ring rotation)
    val shimmerX = (ringRotation / 60f) % 3f - 1f

    // Navigate after reveal completes
    LaunchedEffect(hasCompletedOnboarding) {
        delay(3500)
        when (hasCompletedOnboarding) {
            true -> onNavigateToHome()
            false -> onNavigateToOnboarding()
            null -> { /* Still loading */ }
        }
    }

    // Pre-compute gradient brushes once instead of every frame
    val ringBrush = remember {
        Brush.sweepGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFF8B5CF6).copy(alpha = 0.5f),
                Color.White.copy(alpha = 0.7f),
                Color(0xFF667EEA).copy(alpha = 0.5f),
                Color.Transparent
            )
        )
    }
    val glowOrbBrush = remember {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF667EEA).copy(alpha = 0.4f),
                Color(0xFF764BA2).copy(alpha = 0.2f),
                Color(0xFF8B5CF6).copy(alpha = 0.08f),
                Color.Transparent
            )
        )
    }
    val coreGlowBrush = remember {
        Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.Transparent
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gradients.appBackground)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // === ICON AREA ===
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Rotating gradient arc ring — premium look
                Canvas(
                    modifier = Modifier
                        .size(170.dp)
                        .graphicsLayer { alpha = emojiScale }
                ) {
                    val strokeW = 2.dp.toPx()
                    val pad = strokeW / 2f
                    rotate(ringRotation) {
                        drawArc(
                            brush = ringBrush,
                            startAngle = 0f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round),
                            topLeft = Offset(pad, pad),
                            size = Size(size.width - strokeW, size.height - strokeW)
                        )
                    }
                }

                // Breathing glow orb (no .shadow() — it's very expensive per frame)
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer {
                            scaleX = glowScale
                            scaleY = glowScale
                            alpha = glowAlpha * emojiScale
                        }
                        .background(glowOrbBrush, CircleShape)
                )

                // Soft white core glow
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer { alpha = 0.18f * emojiScale }
                        .background(coreGlowBrush, CircleShape)
                )

                // Microphone emoji
                Text(
                    text = "\uD83C\uDFA4",
                    fontSize = 76.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = emojiScale
                        scaleY = emojiScale
                        translationY = -floatOffset
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // === TITLE ===
            Text(
                text = "Diqto",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha
                    scaleX = titleScale
                    scaleY = titleScale
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // === SHIMMER DIVIDER LINE ===
            val lineWidthDp = 100.dp
            val lineWidthPx = with(LocalDensity.current) { lineWidthDp.toPx() }
            Canvas(
                modifier = Modifier
                    .width(lineWidthDp)
                    .height(1.5.dp)
                    .graphicsLayer { alpha = dividerProgress }
            ) {
                val actualW = size.width * dividerProgress
                val sx = (size.width - actualW) / 2f
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.9f),
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent
                        ),
                        startX = shimmerX * lineWidthPx - lineWidthPx * 0.3f,
                        endX = shimmerX * lineWidthPx + lineWidthPx * 0.3f
                    ),
                    start = Offset(sx, size.height / 2f),
                    end = Offset(sx + actualW, size.height / 2f),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === SUBTITLE ===
            Text(
                text = "Твій голос — твоя сила",
                style = AppTypography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = 1.2.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha
                    translationY = subtitleOffsetY
                }
            )

            Spacer(modifier = Modifier.height(56.dp))

            // === LOADER (only while deciding navigation) ===
            if (hasCompletedOnboarding == null) {
                CircularProgressIndicator(
                    color = Color.White.copy(alpha = 0.5f),
                    strokeWidth = 1.5.dp,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            alpha = subtitleAlpha * 0.8f
                        }
                )
            }
        }
    }
}

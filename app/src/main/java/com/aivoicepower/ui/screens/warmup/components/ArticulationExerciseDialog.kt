package com.aivoicepower.ui.screens.warmup.components

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.screens.warmup.ArticulationExercise
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.utils.performHaptic
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun ArticulationExerciseDialog(
    exercise: ArticulationExercise,
    timerSeconds: Int,
    isTimerRunning: Boolean,
    showCompletionOverlay: Boolean,
    onDismiss: () -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onMarkCompleted: () -> Unit,
    onSkip: () -> Unit,
    currentExerciseNumber: Int = 0,
    totalExercises: Int = 0,
    completedExercises: Int = 0
) {
    val view = LocalView.current
    var showInstructions by remember { mutableStateOf(false) }
    var showExitWarning by remember { mutableStateOf(false) }

    // Handle back button press
    BackHandler(onBack = {
        if (totalExercises > 0) {
            // Quick Warmup - show warning
            showExitWarning = true
        } else {
            // Regular exercise - exit directly
            onDismiss()
        }
    })

    // Fullscreen Box замість Dialog
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        // Контент вправи
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = false) { }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header with X button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exercise.title,
                        style = AppTypography.displayLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.6).sp
                    )

                    // Exit button
                    Text(
                        text = "✕",
                        fontSize = 28.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                performHaptic(view)
                                if (totalExercises > 0) {
                                    // Quick Warmup - show warning
                                    showExitWarning = true
                                } else {
                                    // Regular exercise - exit directly
                                    onDismiss()
                                }
                            }
                            .padding(8.dp)
                    )
                }

                // Overall progress bar (Quick Warmup only)
                if (totalExercises > 0) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Загальний прогрес",
                            style = AppTypography.labelMedium,
                            color = TextColors.onDarkSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        LinearProgressIndicator(
                            progress = { completedExercises.toFloat() / totalExercises },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp),
                            color = Color(0xFF10B981),
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }

                // Circular Timer (CENTER)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Animated progress для плавного руху
                    val targetProgress = 1f - (timerSeconds.toFloat() / exercise.durationSeconds)
                    val animatedProgress by animateFloatAsState(
                        targetValue = targetProgress,
                        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                        label = "timer_progress"
                    )

                    // Timer з круговою progress bar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(200.dp)
                    ) {
                        // Circular progress
                        CircularTimer(
                            progress = animatedProgress,
                            modifier = Modifier.size(200.dp)
                        )

                        // Time display
                        Text(
                            text = "%02d:%02d".format(timerSeconds / 60, timerSeconds % 60),
                            style = AppTypography.displayLarge,
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Instruction text
                    Text(
                        text = exercise.instruction,
                        style = AppTypography.bodyMedium,
                        color = TextColors.onDarkSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                // Bottom controls
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Start/Pause button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (isTimerRunning) Color(0xFFF59E0B).copy(alpha = 0.6f) else Color(0xFF764BA2).copy(alpha = 0.6f),
                                ambientColor = if (isTimerRunning) Color(0xFFF59E0B).copy(alpha = 0.3f) else Color(0xFF764BA2).copy(alpha = 0.3f)
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = if (isTimerRunning) {
                                        listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                                    } else {
                                        listOf(Color(0xFF764BA2), Color(0xFF667EEA))
                                    }
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { performHaptic(view); if (isTimerRunning) onPauseTimer() else onStartTimer() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isTimerRunning) "⏸ Пауза" else "▶ Почати",
                            style = AppTypography.labelLarge,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Skip button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color.Black.copy(alpha = 0.15f)
                                )
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .clickable { performHaptic(view); onSkip() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Пропустити",
                                style = AppTypography.labelLarge,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Complete button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color(0xFF6366F1).copy(alpha = 0.4f)
                                )
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { performHaptic(view); onMarkCompleted() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Готово ✓",
                                style = AppTypography.labelLarge,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Scrim затемнення (тільки коли показано інструкції)
        if (showInstructions) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { /* Блокує кліки */ }
            )
        }

        // Instruction overlay
        if (showInstructions) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 120.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(32.dp),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        )
                        .background(Color.White, RoundedCornerShape(32.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Title
                    Text(
                        text = exercise.title,
                        style = AppTypography.displayLarge,
                        color = TextColors.onLightPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.6).sp,
                        textAlign = TextAlign.Center
                    )

                    // Description section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📋 Як виконувати",
                            style = AppTypography.labelLarge,
                            color = Color(0xFF4ECDC4),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = exercise.instruction,
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        )
                    }

                    // Duration info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ ",
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Тривалість: ${exercise.durationSeconds} сек",
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Start button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { performHaptic(view); showInstructions = false }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Почати вправу",
                            style = AppTypography.labelLarge,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Completion overlay
        if (showCompletionOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { /* Блокує кліки під час completion overlay */ },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(32.dp),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        )
                        .background(Color.White, RoundedCornerShape(32.dp))
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Success icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF10B981), Color(0xFF14B8A6))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Success message
                    Text(
                        text = "Чудова робота!",
                        style = AppTypography.displayLarge,
                        color = TextColors.onLightPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.6).sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Вправу виконано",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Exit Warning Dialog (Quick Warmup only)
        if (showExitWarning) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showExitWarning = false },
                title = {
                    Text(
                        text = "Вийти з розминки?",
                        style = AppTypography.titleLarge,
                        color = TextColors.onLightPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Прогрес Швидкої розминки буде втрачено.",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightSecondary,
                        fontSize = 15.sp
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            showExitWarning = false
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = "Вийти",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); showExitWarning = false }
                    ) {
                        Text(
                            text = "Скасувати",
                            color = Color(0xFF667EEA),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun CircularTimer(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val glowWidth1 = 14.dp.toPx()
        val glowWidth2 = 18.dp.toPx()

        // Background circle - біла лінія
        drawArc(
            color = Color.White,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Outer glow (найбільш прозорий шар)
        drawArc(
            color = Color.White.copy(alpha = 0.15f),
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = glowWidth2, cap = StrokeCap.Round)
        )

        // Inner glow (середній шар)
        drawArc(
            color = Color.White.copy(alpha = 0.3f),
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = glowWidth1, cap = StrokeCap.Round)
        )

        // White outline (тонка біла обводка)
        drawArc(
            color = Color.White.copy(alpha = 0.6f),
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        )

        // Progress arc - яскравіший синій (зверху)
        drawArc(
            color = Color(0xFF7C8FFF),
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

package com.aivoicepower.ui.screens.warmup.components

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import com.aivoicepower.ui.components.breathing.BreathingAnimation
import com.aivoicepower.ui.screens.warmup.BreathingExercise
import com.aivoicepower.ui.screens.warmup.BreathingPattern
import com.aivoicepower.ui.screens.warmup.BreathingPhase
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun BreathingExerciseDialog(
    exercise: BreathingExercise,
    elapsedSeconds: Int,
    totalSeconds: Int,
    currentPhase: BreathingPhase,
    phaseProgress: Float,
    isRunning: Boolean,
    showInstructions: Boolean,
    onDismiss: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onMarkCompleted: () -> Unit,
    onSkip: () -> Unit,
    onHideInstructions: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    // Haptic feedback on phase change
    var lastPhase by remember { mutableStateOf(currentPhase) }

    LaunchedEffect(currentPhase) {
        if (currentPhase != lastPhase && isRunning) {
            triggerHapticFeedback(context)
            lastPhase = currentPhase
        }
    }

    // Remove status bar dimming - make fully transparent
    DisposableEffect(showInstructions) {
        val window = (view.context as? Activity)?.window
        val previousStatusBarColor = window?.statusBarColor
        val previousNavigationBarColor = window?.navigationBarColor

        window?.let {
            // Make status bars fully transparent (no dark overlay)
            it.statusBarColor = android.graphics.Color.TRANSPARENT
            it.navigationBarColor = android.graphics.Color.TRANSPARENT

            // Set light status bars when instructions are shown (white background)
            val insetsController = WindowCompat.getInsetsController(it, view)
            insetsController.isAppearanceLightStatusBars = showInstructions
            insetsController.isAppearanceLightNavigationBars = showInstructions
        }

        onDispose {
            // Restore previous colors when dialog closes
            window?.let {
                previousStatusBarColor?.let { color -> it.statusBarColor = color }
                previousNavigationBarColor?.let { color -> it.navigationBarColor = color }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GradientBackground(content = {})

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = exercise.title,
                        style = AppTypography.displayLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.6).sp
                    )
                }

                // Breathing Animation (CENTER)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Animated Circle
                    BreathingAnimation(
                        phase = currentPhase,
                        progress = phaseProgress,
                        modifier = Modifier.size(280.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Phase text
                    Text(
                        text = getPhaseText(currentPhase),
                        style = AppTypography.headlineLarge,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer
                    Text(
                        text = "%02d:%02d / %02d:%02d".format(
                            elapsedSeconds / 60,
                            elapsedSeconds % 60,
                            totalSeconds / 60,
                            totalSeconds % 60
                        ),
                        style = AppTypography.titleMedium,
                        color = TextColors.onDarkSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Bottom controls
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress
                    LinearProgressIndicator(
                        progress = { elapsedSeconds.toFloat() / totalSeconds },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF6366F1),
                        trackColor = Color(0xFF4C1D95).copy(alpha = 0.3f)
                    )

                    // Pattern description
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Паттерн дихання:",
                            style = AppTypography.labelMedium,
                            color = TextColors.onDarkSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = formatPatternDescription(exercise.pattern),
                            style = AppTypography.bodyMedium,
                            color = TextColors.onDarkPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Start/Pause button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (isRunning) Color(0xFFF59E0B).copy(alpha = 0.4f) else Color(0xFF667EEA).copy(alpha = 0.4f)
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = if (isRunning) {
                                        listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                                    } else {
                                        listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                    }
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { if (isRunning) onPause() else onStart() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isRunning) "⏸ Пауза" else "▶ Почати",
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
                                .clickable { onSkip() }
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
                                .clickable { onMarkCompleted() }
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

        // Instruction overlay (white centered window like RecordingDialog)
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
                        letterSpacing = (-0.6).sp
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
                            text = exercise.description,
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE5E7EB))
                    )

                    // Speech benefit section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎤 Користь для мовлення",
                            style = AppTypography.labelLarge,
                            color = Color(0xFF667EEA),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = exercise.speechBenefit,
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
                            .clickable { onHideInstructions() }
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
    }
}

private fun getPhaseText(phase: BreathingPhase): String {
    return when (phase) {
        BreathingPhase.INHALE -> "Вдих"
        BreathingPhase.INHALE_HOLD -> "Затримка"
        BreathingPhase.EXHALE -> "Видих"
        BreathingPhase.EXHALE_HOLD -> "Затримка"
    }
}

private fun formatPatternDescription(pattern: BreathingPattern): String {
    val parts = mutableListOf<String>()

    parts.add("${pattern.inhaleSeconds}с вдих")
    if (pattern.inhaleHoldSeconds > 0) parts.add("${pattern.inhaleHoldSeconds}с затримка")
    parts.add("${pattern.exhaleSeconds}с видих")
    if (pattern.exhaleHoldSeconds > 0) parts.add("${pattern.exhaleHoldSeconds}с затримка")

    return parts.joinToString(" → ")
}

@Suppress("DEPRECATION")
private fun triggerHapticFeedback(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    } catch (e: Exception) {
        // Ignore vibration errors
    }
}

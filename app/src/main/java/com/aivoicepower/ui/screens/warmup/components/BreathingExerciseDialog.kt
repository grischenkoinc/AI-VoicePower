package com.aivoicepower.ui.screens.warmup.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aivoicepower.ui.screens.warmup.BreathingExercise
import com.aivoicepower.ui.screens.warmup.BreathingPattern
import com.aivoicepower.ui.screens.warmup.BreathingPhase
import com.aivoicepower.ui.components.breathing.BreathingAnimation

@Composable
fun BreathingExerciseDialog(
    exercise: BreathingExercise,
    elapsedSeconds: Int,
    totalSeconds: Int,
    currentPhase: BreathingPhase,
    phaseProgress: Float,
    isRunning: Boolean,
    onDismiss: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onMarkCompleted: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current

    // Haptic feedback on phase change
    var lastPhase by remember { mutableStateOf(currentPhase) }

    LaunchedEffect(currentPhase) {
        if (currentPhase != lastPhase && isRunning) {
            triggerHapticFeedback(context)
            lastPhase = currentPhase
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exercise.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити")
                    }
                }

                // Breathing Animation (CENTER)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Animated Circle
                        BreathingAnimation(
                            phase = currentPhase,
                            progress = phaseProgress,
                            modifier = Modifier.size(250.dp)
                        )

                        // Phase text
                        Text(
                            text = getPhaseText(currentPhase),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Timer
                        Text(
                            text = "%02d:%02d / %02d:%02d".format(
                                elapsedSeconds / 60,
                                elapsedSeconds % 60,
                                totalSeconds / 60,
                                totalSeconds % 60
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Bottom controls
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress
                    LinearProgressIndicator(
                        progress = elapsedSeconds.toFloat() / totalSeconds,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Pattern description
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Паттерн:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = formatPatternDescription(exercise.pattern),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Start/Pause button
                    Button(
                        onClick = if (isRunning) onPause else onStart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isRunning) "⏸️ Пауза" else "▶️ Старт")
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Пропустити")
                        }

                        Button(
                            onClick = onMarkCompleted,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Готово ✓")
                        }
                    }
                }
            }
        }
    }
}

private fun getPhaseText(phase: BreathingPhase): String {
    return when (phase) {
        BreathingPhase.INHALE -> "Вдих..."
        BreathingPhase.INHALE_HOLD -> "Затримка..."
        BreathingPhase.EXHALE -> "Видих..."
        BreathingPhase.EXHALE_HOLD -> "Затримка..."
    }
}

private fun formatPatternDescription(pattern: BreathingPattern): String {
    val parts = mutableListOf<String>()

    parts.add("${pattern.inhaleSeconds} сек вдих")
    if (pattern.inhaleHoldSeconds > 0) parts.add("${pattern.inhaleHoldSeconds} сек затримка")
    parts.add("${pattern.exhaleSeconds} сек видих")
    if (pattern.exhaleHoldSeconds > 0) parts.add("${pattern.exhaleHoldSeconds} сек затримка")

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

package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aivoicepower.ui.screens.warmup.VoiceExercise

@Composable
fun VoiceExerciseDialog(
    exercise: VoiceExercise,
    timerSeconds: Int,
    isTimerRunning: Boolean,
    isAudioPlaying: Boolean,
    onDismiss: () -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onMarkCompleted: () -> Unit,
    onSkip: () -> Unit,
    onPlayAudio: () -> Unit,
    onStopAudio: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${exercise.id}. ${exercise.title}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити")
                    }
                }

                Divider()

                // Instruction
                Text(
                    text = "📝 Інструкція:",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = exercise.instruction,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "⏱️ Тривалість: ${exercise.durationSeconds} секунд",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Audio example placeholder (for Phase 8)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "Аудіо приклад",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        OutlinedButton(
                            onClick = if (isAudioPlaying) onStopAudio else onPlayAudio,
                            enabled = false // Disabled until Phase 8
                        ) {
                            Text(if (isAudioPlaying) "Стоп" else "Відтворити")
                        }
                    }
                }

                // Timer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Time display
                        Text(
                            text = "%02d:%02d".format(timerSeconds / 60, timerSeconds % 60),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        // Progress
                        LinearProgressIndicator(
                            progress = 1f - (timerSeconds.toFloat() / exercise.durationSeconds),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Timer button
                        Button(
                            onClick = if (isTimerRunning) onPauseTimer else onStartTimer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isTimerRunning) "⏸️ Пауза" else "▶️ Старт")
                        }
                    }
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

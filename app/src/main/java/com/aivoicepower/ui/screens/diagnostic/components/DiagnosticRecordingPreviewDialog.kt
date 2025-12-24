package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DiagnosticRecordingPreviewDialog(
    recordingDurationSeconds: Int,
    onRetake: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val minutes = recordingDurationSeconds / 60
    val seconds = recordingDurationSeconds % 60

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Success icon
                Text(
                    text = "✅",
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5
                )

                // Title
                Text(
                    text = "Запис завершено!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                // Duration
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Тривалість:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "%02d:%02d".format(minutes, seconds),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Playback button (disabled for now - Phase 1.3 doesn't implement playback)
                OutlinedButton(
                    onClick = { /* TODO: Phase 2.x - Audio playback */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Text("▶️ Прослухати")
                }

                Divider()

                // Question
                Text(
                    text = "Задоволений результатом?",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetake,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Перезаписати")
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Зберегти ✓")
                    }
                }
            }
        }
    }
}

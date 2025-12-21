package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DiagnosticRecordingDialog(
    recordingSeconds: Int,
    maxDurationSeconds: Int,
    onStopRecording: () -> Unit
) {
    val progress = if (maxDurationSeconds > 0) {
        recordingSeconds.toFloat() / maxDurationSeconds
    } else {
        0f
    }

    val minutes = recordingSeconds / 60
    val seconds = recordingSeconds % 60
    val maxMinutes = maxDurationSeconds / 60
    val maxSeconds = maxDurationSeconds % 60

    Dialog(
        onDismissRequest = { }, // Prevent dismissing by clicking outside
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Title
                Text(
                    text = "🎤 Запис...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Timer
                Text(
                    text = "%02d:%02d / %02d:%02d".format(minutes, seconds, maxMinutes, maxSeconds),
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )

                // Progress bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Animated microphone icon (just text for now)
                Text(
                    text = "🎤",
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = MaterialTheme.typography.displayLarge.fontSize * 2
                )

                Text(
                    text = "Говоріть...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stop button
                Button(
                    onClick = onStopRecording,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("🔴 Зупинити запис")
                }
            }
        }
    }
}

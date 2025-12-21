package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aivoicepower.ui.screens.diagnostic.DiagnosticTask

@Composable
fun DiagnosticInstructionDialog(
    task: DiagnosticTask,
    onDismiss: () -> Unit,
    onStartRecording: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = task.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Divider()

                // Instruction
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📝 Інструкція:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = task.instruction,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Content text if available
                task.contentText?.let { content ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // Duration
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⏱️ Час:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${task.durationSeconds} секунд",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Tips
                if (task.tips.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "💡 Підказки:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        task.tips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Назад")
                    }

                    Button(
                        onClick = onStartRecording,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("Почати запис →")
                    }
                }
            }
        }
    }
}

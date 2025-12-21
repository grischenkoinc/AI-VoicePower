package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.diagnostic.DiagnosticTask
import com.aivoicepower.ui.screens.diagnostic.TaskStatus

@Composable
fun DiagnosticTaskCard(
    task: DiagnosticTask,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnabled = task.status == TaskStatus.PENDING || task.status == TaskStatus.RECORDED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isEnabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = when (task.status) {
                TaskStatus.RECORDED -> MaterialTheme.colorScheme.primaryContainer
                TaskStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.emoji,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = when (task.status) {
                        TaskStatus.RECORDED -> MaterialTheme.colorScheme.onPrimaryContainer
                        TaskStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    }
                )
            }

            // Status indicator
            when (task.status) {
                TaskStatus.RECORDED -> {
                    Text(
                        text = "✅",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                TaskStatus.PENDING -> {
                    if (isEnabled) {
                        Text(
                            text = "▶️ Почати",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                else -> {
                    Text(
                        text = "⏸️",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

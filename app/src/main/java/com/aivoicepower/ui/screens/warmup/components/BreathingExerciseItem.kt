package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.warmup.BreathingExercise
import com.aivoicepower.ui.screens.warmup.BreathingPattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingExerciseItem(
    exercise: BreathingExercise,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = if (isCompleted) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Column {
                    Text(
                        text = "${exercise.id}. ${exercise.title}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = formatPattern(exercise.pattern),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${exercise.durationSeconds} сек",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatPattern(pattern: BreathingPattern): String {
    val parts = mutableListOf<String>()

    parts.add("${pattern.inhaleSeconds}с вдих")
    if (pattern.inhaleHoldSeconds > 0) parts.add("${pattern.inhaleHoldSeconds}с затримка")
    parts.add("${pattern.exhaleSeconds}с видих")
    if (pattern.exhaleHoldSeconds > 0) parts.add("${pattern.exhaleHoldSeconds}с затримка")

    return parts.joinToString(", ")
}

package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.warmup.WarmupCategory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WarmupCategoryCard(
    category: WarmupCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = category.icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                Column {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${category.exerciseCount} вправ \u2022 ~${category.estimatedMinutes} хвилини",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Description
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodyMedium
            )

            // Last completed
            Text(
                text = "Останнє: ${formatLastCompleted(category.lastCompletedDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Progress
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = category.completionRate,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                )
                Text(
                    text = "${(category.completionRate * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private fun formatLastCompleted(date: String?): String {
    if (date == null) return "ніколи"

    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val completedDate = sdf.parse(date) ?: return "невідомо"
        val today = Date()

        val diffInDays = ((today.time - completedDate.time) / (1000 * 60 * 60 * 24)).toInt()

        when (diffInDays) {
            0 -> "сьогодні"
            1 -> "вчора"
            in 2..4 -> "$diffInDays дні тому"
            else -> "$diffInDays днів тому"
        }
    } catch (e: Exception) {
        "невідомо"
    }
}

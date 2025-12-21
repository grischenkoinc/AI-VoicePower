package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.diagnostic.MetricDisplay

@Composable
fun SkillScoreCard(
    metric: MetricDisplay,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = metric.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${metric.score} / 100",
                style = MaterialTheme.typography.titleMedium,
                color = getScoreColor(metric.score)
            )
        }

        // Progress bar
        LinearProgressIndicator(
            progress = metric.score / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = getScoreColor(metric.score)
        )

        // Description
        Text(
            text = metric.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun getScoreColor(score: Int) = when {
    score >= 85 -> MaterialTheme.colorScheme.primary
    score >= 70 -> MaterialTheme.colorScheme.tertiary
    score >= 50 -> MaterialTheme.colorScheme.secondary
    else -> MaterialTheme.colorScheme.error
}

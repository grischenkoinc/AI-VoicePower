package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.diagnostic.MetricDisplay

@Composable
fun SkillRadarChart(
    metrics: List<MetricDisplay>,
    modifier: Modifier = Modifier
) {
    // Simplified radar chart placeholder
    // TODO: Implement proper radar chart with Canvas or use library

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📊 Профіль навичок",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Simple Canvas placeholder
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f

            // Draw background circles
            for (i in 1..4) {
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    radius = radius * i / 4,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Draw metrics as points (simplified visualization)
            // TODO: Proper radar chart implementation with metrics data
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Show metrics as text for now
        Text(
            text = "Загальний рівень: ${metrics.map { it.score }.average().toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

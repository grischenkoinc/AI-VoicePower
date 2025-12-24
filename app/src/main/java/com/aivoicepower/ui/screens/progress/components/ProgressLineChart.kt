package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.aivoicepower.ui.screens.progress.DailyProgress

@Composable
fun ProgressLineChart(
    weeklyProgress: List<DailyProgress>,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxSize()) {
        if (weeklyProgress.isEmpty()) return@Canvas

        val maxMinutes = weeklyProgress.maxOf { it.minutes }.coerceAtLeast(1)
        val width = size.width
        val height = size.height
        val stepX = width / (weeklyProgress.size - 1).coerceAtLeast(1)

        // Draw line
        val path = Path()
        weeklyProgress.forEachIndexed { index, progress ->
            val x = index * stepX
            val y = height - (progress.minutes.toFloat() / maxMinutes * height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3f)
        )

        // Draw points
        weeklyProgress.forEachIndexed { index, progress ->
            val x = index * stepX
            val y = height - (progress.minutes.toFloat() / maxMinutes * height)

            drawCircle(
                color = color,
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
}

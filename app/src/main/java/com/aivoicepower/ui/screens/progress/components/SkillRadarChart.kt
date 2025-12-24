package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.aivoicepower.domain.model.user.SkillType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SkillRadarChart(
    skillLevels: Map<SkillType, Int>,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.8f
        val numberOfSkills = skillLevels.size.coerceAtLeast(1)
        val angleStep = (2 * PI / numberOfSkills).toFloat()

        // Draw grid circles
        for (i in 1..4) {
            val r = radius * i / 4
            drawCircle(
                color = gridColor,
                radius = r,
                center = center,
                style = Stroke(width = 1f)
            )
        }

        // Draw axes
        skillLevels.keys.forEachIndexed { index, _ ->
            val angle = angleStep * index - PI.toFloat() / 2
            val end = Offset(
                center.x + radius * cos(angle),
                center.y + radius * sin(angle)
            )
            drawLine(
                color = gridColor,
                start = center,
                end = end,
                strokeWidth = 1f
            )
        }

        // Draw data polygon
        if (skillLevels.isNotEmpty()) {
            val path = Path()
            skillLevels.values.forEachIndexed { index, level ->
                val angle = angleStep * index - PI.toFloat() / 2
                val distance = radius * (level / 100f)
                val point = Offset(
                    center.x + distance * cos(angle),
                    center.y + distance * sin(angle)
                )

                if (index == 0) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }
            }
            path.close()

            // Fill
            drawPath(
                path = path,
                color = color.copy(alpha = 0.3f)
            )

            // Stroke
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 2f)
            )
        }
    }
}

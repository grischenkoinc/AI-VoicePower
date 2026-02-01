package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import kotlin.math.cos
import kotlin.math.sin

data class RadarMetric(
    val label: String,
    val value: Int
)

@Composable
fun SkillRadarChart(
    skillLevels: Map<SkillType, Int>,
    modifier: Modifier = Modifier
) {
    // Convert Map to List of RadarMetric in specific order
    val metrics = listOf(
        RadarMetric("Дикція", skillLevels[SkillType.DICTION] ?: 0),
        RadarMetric("Темп", skillLevels[SkillType.TEMPO] ?: 0),
        RadarMetric("Інтонація", skillLevels[SkillType.INTONATION] ?: 0),
        RadarMetric("Гучність", skillLevels[SkillType.VOLUME] ?: 0),
        RadarMetric("Структура", skillLevels[SkillType.STRUCTURE] ?: 0),
        RadarMetric("Впевненість", skillLevels[SkillType.CONFIDENCE] ?: 0),
        RadarMetric("Без паразитів", skillLevels[SkillType.FILLER_WORDS] ?: 0)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas з КРУГЛИМ радаром
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.85f
            val angleStep = 360f / metrics.size

            // Background CIRCLES
            for (i in 1..5) {
                val circleRadius = radius * i / 5
                drawCircle(
                    color = Color(0xFFE5E7EB),
                    radius = circleRadius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }

            // Axes
            metrics.forEachIndexed { index, _ ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val endX = center.x + (radius * cos(angle)).toFloat()
                val endY = center.y + (radius * sin(angle)).toFloat()

                drawLine(
                    color = Color(0xFFE5E7EB),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }

            // Data polygon
            val dataPath = Path()
            metrics.forEachIndexed { index, metric ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val distance = radius * (metric.value / 100f)
                val x = center.x + (distance * cos(angle)).toFloat()
                val y = center.y + (distance * sin(angle)).toFloat()

                if (index == 0) dataPath.moveTo(x, y)
                else dataPath.lineTo(x, y)
            }
            dataPath.close()

            drawPath(path = dataPath, color = Color(0xFF667EEA).copy(alpha = 0.3f))
            drawPath(path = dataPath, color = Color(0xFF667EEA), style = Stroke(width = 3f))

            // Points
            metrics.forEachIndexed { index, metric ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val distance = radius * (metric.value / 100f)
                val x = center.x + (distance * cos(angle)).toFloat()
                val y = center.y + (distance * sin(angle)).toFloat()

                drawCircle(color = Color(0xFF667EEA), radius = 6f, center = Offset(x, y))
                drawCircle(color = Color.White, radius = 3f, center = Offset(x, y))
            }
        }

        // Мітки з компенсацією розміру
        val labelDistance = 118.dp
        val angleStep = 360f / metrics.size

        metrics.forEachIndexed { index, metric ->
            val angleDegrees = angleStep * index - 90
            val angleRadians = Math.toRadians(angleDegrees.toDouble())

            // Базовий offset від центру
            val baseOffsetX = (labelDistance.value * cos(angleRadians)).dp
            val baseOffsetY = (labelDistance.value * sin(angleRadians)).dp

            // Розмір мітки
            val labelWidth = if (metric.label == "Без паразитів") 75.dp else 65.dp
            val labelHeight = if (metric.label == "Без паразитів") 40.dp else 30.dp

            // Компенсація залежно від кута
            val correctionX = when {
                angleDegrees in -30f..30f -> 0.dp
                angleDegrees in 30f..90f -> -(labelWidth / 2)
                angleDegrees in 90f..150f -> -(labelWidth / 2)
                angleDegrees in 150f..210f -> 0.dp
                angleDegrees in 210f..270f -> (labelWidth / 2)
                else -> (labelWidth / 2)
            }

            val correctionY = when {
                angleDegrees in -90f..-30f -> (labelHeight / 2)
                angleDegrees in -30f..30f -> 0.dp
                angleDegrees in 30f..90f -> -(labelHeight / 2)
                angleDegrees in 90f..150f -> -(labelHeight / 2)
                angleDegrees in 150f..210f -> -(labelHeight / 2)
                else -> 0.dp
            }

            RadarLabel(
                label = metric.label,
                value = metric.value,
                isLong = metric.label == "Без паразитів",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = baseOffsetX + correctionX,
                        y = baseOffsetY + correctionY
                    )
            )
        }
    }
}

@Composable
private fun RadarLabel(
    label: String,
    value: Int,
    isLong: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(if (isLong) 75.dp else 65.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = TextColors.onLightSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = if (isLong) 2 else 1,
            lineHeight = if (isLong) 12.sp else 10.sp,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "$value",
            style = AppTypography.bodyMedium,
            color = Color(0xFF667EEA),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

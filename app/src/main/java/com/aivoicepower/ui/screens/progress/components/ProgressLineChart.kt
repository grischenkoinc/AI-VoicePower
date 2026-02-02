package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.screens.progress.DailyProgress
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ProgressLineChart(
    weeklyProgress: List<DailyProgress>,
    modifier: Modifier = Modifier
) {
    // Check if there's any activity (exercises or minutes)
    val hasActivity = weeklyProgress.isNotEmpty() && weeklyProgress.any { it.exercises > 0 || it.minutes > 0 }

    if (!hasActivity) {
        // Empty state
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Почніть практикувати щоб побачити графік активності",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val lineColor = Color(0xFF6366F1)
    val gradientStartColor = Color(0xFF6366F1).copy(alpha = 0.3f)
    val gradientEndColor = Color(0xFF8B5CF6).copy(alpha = 0.1f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Chart with gradient fill
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val maxMinutes = weeklyProgress.maxOf { it.minutes }.coerceAtLeast(10)
            val width = size.width - 40f
            val height = size.height - 40f
            val stepX = width / (weeklyProgress.size - 1).coerceAtLeast(1)

            // Draw filled area under line with gradient
            val fillPath = Path()
            weeklyProgress.forEachIndexed { index, progress ->
                val x = 20f + index * stepX
                val y = 20f + (height - (progress.minutes.toFloat() / maxMinutes * height))

                if (index == 0) {
                    fillPath.moveTo(x, y)
                } else {
                    fillPath.lineTo(x, y)
                }
            }
            // Complete the path to fill
            fillPath.lineTo(20f + width, 20f + height)
            fillPath.lineTo(20f, 20f + height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientStartColor, gradientEndColor)
                ),
                style = Fill
            )

            // Draw line
            val linePath = Path()
            weeklyProgress.forEachIndexed { index, progress ->
                val x = 20f + index * stepX
                val y = 20f + (height - (progress.minutes.toFloat() / maxMinutes * height))

                if (index == 0) {
                    linePath.moveTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                }
            }

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 4f)
            )

            // Draw points with glow effect
            weeklyProgress.forEachIndexed { index, progress ->
                val x = 20f + index * stepX
                val y = 20f + (height - (progress.minutes.toFloat() / maxMinutes * height))

                // Outer glow
                drawCircle(
                    color = lineColor.copy(alpha = 0.3f),
                    radius = 10f,
                    center = Offset(x, y)
                )

                // Main point
                drawCircle(
                    color = lineColor,
                    radius = 7f,
                    center = Offset(x, y)
                )

                // Inner highlight
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = Offset(x, y)
                )
            }
        }

        // Day labels and values
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weeklyProgress.forEach { progress ->
                val dayOfWeek = try {
                    LocalDate.parse(progress.date).dayOfWeek
                        .getDisplayName(TextStyle.SHORT, Locale("uk"))
                        .uppercase()
                } catch (e: Exception) {
                    ""
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Minutes value
                    if (progress.minutes > 0) {
                        Text(
                            text = "${progress.minutes}хв",
                            style = AppTypography.labelSmall,
                            color = Color(0xFF6366F1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Day label
                    Text(
                        text = dayOfWeek,
                        style = AppTypography.labelSmall,
                        color = if (progress.minutes > 0)
                            TextColors.onLightPrimary
                        else
                            TextColors.onLightSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (progress.minutes > 0)
                            FontWeight.Bold
                        else
                            FontWeight.Medium
                    )
                }
            }
        }

        // Summary
        val totalMinutes = weeklyProgress.sumOf { it.minutes }
        val activeDays = weeklyProgress.count { it.minutes > 0 }

        if (totalMinutes > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ $activeDays з 7 днів активні",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Всього: ${totalMinutes}хв",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF6366F1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

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
    val lineColor = Color(0xFF667EEA)
    val pointColor = Color(0xFF667EEA)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Chart
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
        ) {
            if (weeklyProgress.isEmpty()) return@Canvas

            val maxMinutes = weeklyProgress.maxOf { it.minutes }.coerceAtLeast(1)
            val width = size.width - 40f // Padding for labels
            val height = size.height - 20f // Padding for bottom labels
            val stepX = width / (weeklyProgress.size - 1).coerceAtLeast(1)

            // Draw line
            val path = Path()
            weeklyProgress.forEachIndexed { index, progress ->
                val x = 20f + index * stepX
                val y = 10f + (height - (progress.minutes.toFloat() / maxMinutes * height))

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3f)
            )

            // Draw points
            weeklyProgress.forEachIndexed { index, progress ->
                val x = 20f + index * stepX
                val y = 10f + (height - (progress.minutes.toFloat() / maxMinutes * height))

                drawCircle(
                    color = pointColor,
                    radius = 6f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = Offset(x, y)
                )
            }
        }

        // Day labels
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
                Text(
                    text = dayOfWeek,
                    style = AppTypography.labelSmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

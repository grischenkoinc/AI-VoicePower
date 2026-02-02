package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun ProgressOverviewCard(
    overallLevel: Int,
    currentStreak: Int,
    longestStreak: Int,
    totalExercises: Int,
    totalMinutes: Int,
    totalRecordings: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF6366F1).copy(alpha = 0.3f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6366F1),
                        Color(0xFF8B5CF6)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "✨",
                fontSize = 28.sp
            )
            Text(
                text = "Твій прогрес",
                style = AppTypography.titleLarge,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
        }

        // Main Level Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Progress with Level
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                // Outer glow circle
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            CircleShape
                        )
                )

                // Progress circle
                Canvas(modifier = Modifier.size(120.dp)) {
                    val sweepAngle = (overallLevel / 100f) * 360f

                    // Background track
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Progress arc
                    drawArc(
                        color = Color.White,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Level text
                Text(
                    text = "$overallLevel",
                    style = AppTypography.displayLarge,
                    color = Color.White,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                )
            }

            // Level description
            Column(
                modifier = Modifier.weight(1f).padding(start = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = getLevelTitle(overallLevel),
                    style = AppTypography.titleLarge,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp
                )
                Text(
                    text = getLevelDescription(overallLevel),
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.3f))
        )

        // Streaks Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current Streak
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔥",
                    fontSize = 40.sp
                )
                Text(
                    text = "$currentStreak",
                    style = AppTypography.headlineMedium,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "днів поспіль",
                    style = AppTypography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 16.sp
                )
            }

            // Longest Streak
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🏆",
                    fontSize = 40.sp
                )
                Text(
                    text = "$longestStreak",
                    style = AppTypography.headlineMedium,
                    color = Color(0xFFFBBF24),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "рекорд",
                    style = AppTypography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 16.sp
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.3f))
        )

        // Statistics Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📊",
                    fontSize = 24.sp
                )
                Text(
                    text = "Статистика",
                    style = AppTypography.titleMedium,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    icon = "✏️",
                    value = "$totalExercises",
                    label = "вправ",
                    modifier = Modifier.weight(1f)
                )

                StatBox(
                    icon = "⏰",
                    value = "$totalMinutes",
                    label = "хвилин",
                    modifier = Modifier.weight(1f)
                )

                StatBox(
                    icon = "🎤",
                    value = "$totalRecordings",
                    label = "записів",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatBox(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 32.sp
        )
        Text(
            text = value,
            style = AppTypography.titleLarge,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun getLevelTitle(level: Int): String {
    return when {
        level < 20 -> "🌱 Початківець"
        level < 40 -> "📚 Практикуючий"
        level < 60 -> "💡 Досвідчений"
        level < 80 -> "⭐ Майстер"
        else -> "👑 Професіонал"
    }
}

private fun getLevelDescription(level: Int): String {
    return when {
        level < 20 -> "Ти тільки починаєш свій шлях! Продовжуй практикувати."
        level < 40 -> "Добре просуваєшся! Навички помітно покращуються."
        level < 60 -> "Чудові результати! Ти вже багато досяг."
        level < 80 -> "Вражаючий прогрес! Майже на вершині майстерності."
        else -> "Ти досяг вершини! Продовжуй підтримувати рівень."
    }
}

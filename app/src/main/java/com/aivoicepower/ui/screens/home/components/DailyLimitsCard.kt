package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DailyLimitsCard(
    remainingAnalyses: Int,
    maxAnalyses: Int,
    remainingImprovAnalyses: Int,
    maxImprovAnalyses: Int,
    remainingAiMessages: Int,
    maxAiMessages: Int,
    modifier: Modifier = Modifier
) {
    val usedAnalyses = (maxAnalyses - remainingAnalyses).coerceAtLeast(0)
    val usedImprov = (maxImprovAnalyses - remainingImprovAnalyses).coerceAtLeast(0)
    val usedAi = (maxAiMessages - remainingAiMessages).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Сьогодні",
            color = Color(0xFF111827),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        LimitRow(
            emoji = "\uD83D\uDD2C",
            label = "Аналізів",
            used = usedAnalyses,
            max = maxAnalyses,
            gradientColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
        )

        LimitRow(
            emoji = "\uD83C\uDFAD",
            label = "Імпровізацій",
            used = usedImprov,
            max = maxImprovAnalyses,
            gradientColors = listOf(Color(0xFFEC4899), Color(0xFFDB2777))
        )

        LimitRow(
            emoji = "\uD83D\uDCAC",
            label = "AI повідомлень",
            used = usedAi,
            max = maxAiMessages,
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
        )
    }
}

@Composable
private fun LimitRow(
    emoji: String,
    label: String,
    used: Int,
    max: Int,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    val remaining = max - used
    val progress = if (max > 0) used.toFloat() / max.toFloat() else 0f
    val countColor = when {
        remaining > (max * 0.6f) -> Color(0xFF22C55E)
        remaining > (max * 0.2f) -> Color(0xFFF59E0B)
        remaining > 0 -> Color(0xFFEF4444)
        else -> Color(0xFF6B7280)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gradient icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = gradientColors.first().copy(alpha = 0.25f)
                )
                .background(
                    Brush.linearGradient(gradientColors),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 18.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    color = Color(0xFF4B5563),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$used/$max",
                    color = countColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFF3F4F6))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Brush.horizontalGradient(gradientColors))
                )
            }
        }
    }
}

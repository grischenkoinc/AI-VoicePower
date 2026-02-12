package com.aivoicepower.ui.screens.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    modifier: Modifier = Modifier
) {
    val usedAnalyses = (maxAnalyses - remainingAnalyses).coerceAtLeast(0)
    val usedImprov = (maxImprovAnalyses - remainingImprovAnalyses).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.18f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Сьогодні",
                color = Color(0xFF111827),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Безкоштовний план",
                color = Color(0xFF8B5CF6),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Analysis limit row
        LimitRow(
            emoji = "\uD83D\uDD2C",
            label = "Аналізів",
            used = usedAnalyses,
            max = maxAnalyses
        )

        // Improvisation limit row
        LimitRow(
            emoji = "\uD83C\uDFAD",
            label = "Імпровізацій",
            used = usedImprov,
            max = maxImprovAnalyses
        )
    }
}

@Composable
private fun LimitRow(
    emoji: String,
    label: String,
    used: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val remaining = max - used
    val progress = if (max > 0) used.toFloat() / max.toFloat() else 0f
    val color = when {
        remaining > (max * 0.6f) -> Color(0xFF22C55E) // green
        remaining > (max * 0.2f) -> Color(0xFFF59E0B) // orange
        remaining > 0 -> Color(0xFFEF4444) // red
        else -> Color(0xFF6B7280) // grey
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 18.sp
        )

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
                    color = color,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFF3F4F6))
            ) {
                // Filled portion (used)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    color.copy(alpha = 0.7f),
                                    color
                                )
                            )
                        )
                )
            }
        }
    }
}

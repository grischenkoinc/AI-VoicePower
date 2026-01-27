package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.screens.warmup.WarmupStats
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun WarmupStatsCard(
    stats: WarmupStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.3f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x73667EEA), // 45% opacity
                        Color(0x59764BA2)  // 35% opacity
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak
            StatItem(
                icon = "🔥",
                label = "Streak",
                value = "${stats.currentStreak} днів"
            )

            VerticalDividerCompat()

            // Today
            StatItem(
                icon = "⏱️",
                label = "Сьогодні",
                value = "${stats.todayMinutes} хв"
            )

            VerticalDividerCompat()

            // Total
            StatItem(
                icon = "📊",
                label = "Всього",
                value = "${stats.totalCompletions}"
            )

            VerticalDividerCompat()

            // Level
            StatItem(
                icon = "⭐",
                label = "Рівень",
                value = "${stats.level}"
            )
        }
    }
}

@Composable
private fun VerticalDividerCompat() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

@Composable
private fun StatItem(
    icon: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = TextColors.onDarkSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = AppTypography.titleSmall,
            color = TextColors.onDarkPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

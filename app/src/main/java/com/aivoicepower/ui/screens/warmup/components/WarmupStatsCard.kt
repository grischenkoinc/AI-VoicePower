package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.warmup.WarmupStats

@Composable
fun WarmupStatsCard(
    stats: WarmupStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak
            StatItem(
                icon = "\uD83D\uDD25", // 🔥
                label = "Streak",
                value = "${stats.currentStreak} днів"
            )

            VerticalDividerCompat()

            // Today
            StatItem(
                icon = "\u23F1\uFE0F", // ⏱️
                label = "Сьогодні",
                value = "${stats.todayMinutes} хв"
            )

            VerticalDividerCompat()

            // Total
            StatItem(
                icon = "\uD83D\uDCCA", // 📊
                label = "Всього",
                value = "${stats.totalCompletions}"
            )

            VerticalDividerCompat()

            // Level
            StatItem(
                icon = "\u2B50", // ⭐
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
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
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
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

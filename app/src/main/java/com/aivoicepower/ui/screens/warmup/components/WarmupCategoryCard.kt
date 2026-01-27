package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.screens.warmup.WarmupCategory
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun WarmupCategoryCard(
    category: WarmupCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = category.icon,
                fontSize = 32.sp
            )
            Column {
                Text(
                    text = category.title,
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${category.exerciseCount} вправ \u2022 ~${category.estimatedMinutes} хвилини",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Description
        Text(
            text = category.description,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        // Progress
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { category.completionRate },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFFE5E7EB)
            )
            Text(
                text = "${(category.completionRate * 100).toInt()}%",
                style = AppTypography.labelSmall,
                color = TextColors.onLightPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

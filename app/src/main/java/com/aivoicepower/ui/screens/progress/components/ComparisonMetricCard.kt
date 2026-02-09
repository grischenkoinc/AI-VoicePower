package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun ComparisonMetricCard(
    skillName: String,
    initialValue: Int,
    currentValue: Int,
    improvement: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.18f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = skillName,
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(
                        text = "Було",
                        style = AppTypography.labelSmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$initialValue",
                        style = AppTypography.bodyLarge,
                        color = TextColors.onLightMuted,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "Зараз",
                        style = AppTypography.labelSmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currentValue",
                        style = AppTypography.bodyLarge,
                        color = Color(0xFF667EEA),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        if (improvement > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "+$improvement%",
                    style = AppTypography.titleMedium,
                    color = Color(0xFF10B981),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

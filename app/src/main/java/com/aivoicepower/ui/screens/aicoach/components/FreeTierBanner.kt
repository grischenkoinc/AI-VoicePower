package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.SemanticColors

@Composable
fun FreeTierBanner(
    messagesRemaining: Int,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bannerShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(bannerShape)
            .background(
                color = SemanticColors.warning.copy(alpha = 0.1f)
            )
            .border(
                width = 1.dp,
                color = SemanticColors.warning.copy(alpha = 0.25f),
                shape = bannerShape
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "\u26A0\uFE0F Залишилось $messagesRemaining повідомлень",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "Оновіться для необмеженого доступу",
                    style = TextStyle(fontSize = 12.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // Gradient upgrade button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667EEA),
                                Color(0xFF8B5CF6)
                            )
                        )
                    )
            ) {
                TextButton(onClick = onUpgradeClick) {
                    Text(
                        text = "Оновити",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

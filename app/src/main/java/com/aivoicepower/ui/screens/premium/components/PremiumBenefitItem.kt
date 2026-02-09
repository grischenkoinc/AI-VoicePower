package com.aivoicepower.ui.screens.premium.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
fun PremiumBenefitItem(
    emoji: String,
    title: String,
    description: String,
    iconBackgroundColors: List<Color>,
    useWhiteBackground: Boolean = false,
    borderColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(13.dp),
                    spotColor = if (useWhiteBackground || borderColor != null)
                        Color.Black.copy(alpha = 0.06f)
                    else iconBackgroundColors.first().copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(13.dp))
                .then(
                    if (borderColor != null) {
                        Modifier
                            .background(Color.White, RoundedCornerShape(13.dp))
                            .border(
                                width = 2.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(13.dp)
                            )
                    } else if (useWhiteBackground) {
                        Modifier.background(Color.White, RoundedCornerShape(13.dp))
                    } else {
                        Modifier.background(
                            Brush.linearGradient(colors = iconBackgroundColors),
                            RoundedCornerShape(13.dp)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                lineHeight = 16.sp
            )
        }

        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    Color(0xFF10B981).copy(alpha = 0.12f),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

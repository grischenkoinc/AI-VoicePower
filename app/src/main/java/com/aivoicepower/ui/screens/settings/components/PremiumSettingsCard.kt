package com.aivoicepower.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
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
fun PremiumSettingsCard(
    isPremium: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            // Outer glow effect - purple shadow
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF764BA2).copy(alpha = 0.30f),
                ambientColor = Color(0xFF667EEA).copy(alpha = 0.15f)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(primaryGradient, RoundedCornerShape(20.dp))
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.25f),
                    RoundedCornerShape(20.dp)
                )
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Star icon - more prominent with glow background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Color.White.copy(alpha = 0.22f),
                        RoundedCornerShape(14.dp)
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Преміум",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (isPremium) {
                        Spacer(modifier = Modifier.width(8.dp))
                        // PRO badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Color(0xFFFBBF24),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E1730),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                if (isPremium) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4ADE80),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Активний",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4ADE80)
                        )
                    }
                } else {
                    Text(
                        text = "Отримати повний доступ",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            if (isPremium) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // "Отримати PRO" CTA with arrow
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.25f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Отримати PRO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

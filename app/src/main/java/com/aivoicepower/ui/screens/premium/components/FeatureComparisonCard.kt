package com.aivoicepower.ui.screens.premium.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FeatureComparisonCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Порівняння версій",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Функція",
                modifier = Modifier.weight(1f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9CA3AF)
            )
            Text(
                text = "Free",
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9CA3AF)
            )
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667EEA).copy(alpha = 0.12f),
                                Color(0xFF764BA2).copy(alpha = 0.12f)
                            )
                        ),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PRO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF764BA2)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = Color(0xFFF3F4F6),
            thickness = 1.dp
        )

        ComparisonRow("Розминка", free = true, premium = true)
        ComparisonRow("Діагностика", freeText = "1 раз", premiumText = "Безліміт")
        ComparisonRow("Уроки курсів", freeText = "7/курс", premiumText = "Всі")
        ComparisonRow("Імпровізація", freeText = "1/день", premiumText = "Безліміт")
        ComparisonRow("AI-тренер", freeText = "10/день", premiumText = "Безліміт")
        ComparisonRow("Без реклами", free = false, premium = true)
    }
}

@Composable
private fun ComparisonRow(
    feature: String,
    free: Boolean? = null,
    premium: Boolean? = null,
    freeText: String? = null,
    premiumText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = Color(0xFF4B5563)
        )

        // Free column
        Box(
            modifier = Modifier.width(60.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                freeText != null -> {
                    Text(
                        text = freeText,
                        fontSize = 12.sp,
                        color = Color(0xFFB0B0B0),
                        textAlign = TextAlign.Center
                    )
                }
                free == true -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFFD1D5DB),
                        modifier = Modifier.size(18.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color(0xFFE5E7EB),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Premium column
        Box(
            modifier = Modifier.width(72.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                premiumText != null -> {
                    Text(
                        text = premiumText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981),
                        textAlign = TextAlign.Center
                    )
                }
                premium == true -> {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(
                                Color(0xFF10B981).copy(alpha = 0.12f),
                                RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color(0xFFE5E7EB),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

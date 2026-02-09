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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.modifiers.glassBackground

/**
 * Feature comparison card showing Free vs PRO columns
 * with a glass morphism style.
 */
@Composable
fun FeatureComparisonCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .glassBackground(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White.copy(alpha = 0.1f),
                borderColor = Color.White.copy(alpha = 0.2f)
            )
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Порівняння версій",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
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
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = "Free",
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.5f)
            )
            // PRO column header with gradient background
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667EEA).copy(alpha = 0.3f),
                                Color(0xFF764BA2).copy(alpha = 0.3f)
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
                    color = Color(0xFFFBBF24)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = Color.White.copy(alpha = 0.1f),
            thickness = 1.dp
        )

        // Feature rows
        ComparisonRow("Розминка", free = true, premium = true)
        ComparisonRow("Діагностика", freeText = "1 раз", premiumText = "Безліміт")
        ComparisonRow("Уроки курсів", freeText = "7/курс", premiumText = "Всі")
        ComparisonRow("Імпровізація", freeText = "3/день", premiumText = "Безліміт")
        ComparisonRow("AI-тренер", freeText = "10/день", premiumText = "Безліміт")
        ComparisonRow("Прогрес", freeText = "Базовий", premiumText = "Повний")
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
            color = Color.White.copy(alpha = 0.8f)
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
                        color = Color.White.copy(alpha = 0.45f),
                        textAlign = TextAlign.Center
                    )
                }
                free == true -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f),
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
                            .size(20.dp)
                            .background(
                                Color(0xFF10B981).copy(alpha = 0.2f),
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
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

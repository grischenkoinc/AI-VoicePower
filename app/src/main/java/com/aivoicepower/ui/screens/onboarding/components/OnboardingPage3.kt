package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun OnboardingPage3(
    selectedMinutes: Int,
    onMinutesSelected: (Int) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(top = 48.dp, bottom = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Text(
                text = "Скільки часу готовий приділяти?",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            // Time Options Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeOption(
                    minutes = 5,
                    title = "5 хвилин на день",
                    subtitle = "Швидкі вправи між справами",
                    isSelected = selectedMinutes == 5,
                    onSelect = { onMinutesSelected(5) }
                )

                TimeOption(
                    minutes = 15,
                    title = "15 хвилин на день",
                    subtitle = "Оптимально для результату",
                    isSelected = selectedMinutes == 15,
                    isRecommended = true,
                    onSelect = { onMinutesSelected(15) }
                )

                TimeOption(
                    minutes = 30,
                    title = "30 хвилин на день",
                    subtitle = "Прискорений прогрес",
                    isSelected = selectedMinutes == 30,
                    onSelect = { onMinutesSelected(30) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ти завжди зможеш змінити це в налаштуваннях",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "← Назад",
                            style = AppTypography.titleMedium,
                            color = TextColors.onLightPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Next button
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF667EEA).copy(alpha = 0.4f))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onNextClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Далі →",
                            style = AppTypography.titleMedium,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                PageIndicator(currentPage = 2, totalPages = 4)
            }
        }
    }
}

@Composable
private fun TimeOption(
    minutes: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    isRecommended: Boolean = false,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0xFF667EEA).copy(alpha = 0.1f)
                else Color(0xFFF8F9FA)
            )
            .then(
                if (isSelected) Modifier.border(2.dp, Color(0xFF667EEA), RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable(onClick = onSelect)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = AppTypography.titleMedium,
                    color = if (isSelected) Color(0xFF667EEA) else TextColors.onLightPrimary,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                )

                if (isRecommended && !isSelected) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF10B981),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✓ Рекомендовано",
                            style = AppTypography.labelSmall,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = subtitle,
                style = AppTypography.bodySmall,
                color = if (isSelected) Color(0xFF667EEA).copy(alpha = 0.8f) else TextColors.onLightSecondary,
                fontSize = 14.sp
            )
        }
    }
}

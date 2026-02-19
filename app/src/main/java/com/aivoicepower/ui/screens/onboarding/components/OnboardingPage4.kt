package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
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
import com.aivoicepower.ui.theme.modifiers.pulseAnimation

@Composable
fun OnboardingPage4(
    onStartDiagnostic: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Почнемо з діагностики! 🎯",
                    style = AppTypography.displayLarge,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Швидкий тест (5 хвилин) визначить твій рівень та створить персоналізований план",
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // Metrics Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Що будемо оцінювати:",
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                MetricItem(icon = Icons.Default.GraphicEq, text = "Чіткість дикції")
                MetricItem(icon = Icons.Default.Speed, text = "Темп мовлення")
                MetricItem(icon = Icons.Default.MusicNote, text = "Інтонація та виразність")
                MetricItem(icon = Icons.AutoMirrored.Filled.VolumeUp, text = "Гучність голосу")
                MetricItem(icon = Icons.AutoMirrored.Filled.ListAlt, text = "Структура думок")
                MetricItem(icon = Icons.Default.CheckCircle, text = "Впевненість")
                MetricItem(icon = Icons.Default.Block, text = "Слова-паразити")
            }

            // Requirements Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFFFFBEB),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💡 Знадобиться:",
                    style = AppTypography.titleSmall,
                    color = Color(0xFF92400E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "• 5 хвилин часу\n• Тихе місце\n• Дозвіл на мікрофон",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF92400E),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start diagnostic button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .pulseAnimation(scaleFrom = 1f, scaleTo = 1.03f, duration = 2000)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF667EEA).copy(alpha = 0.4f))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onStartDiagnostic),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Почати діагностику →",
                        style = AppTypography.titleMedium,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Back button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "← Назад",
                        style = AppTypography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                PageIndicator(currentPage = 3, totalPages = 4)
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF667EEA),
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

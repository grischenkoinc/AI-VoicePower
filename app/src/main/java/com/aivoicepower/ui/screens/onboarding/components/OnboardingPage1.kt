package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

@Composable
fun OnboardingPage1(
    onNextClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(top = 32.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎤",
                    fontSize = 64.sp
                )

                Text(
                    text = "AI VoicePower",
                    style = AppTypography.displayLarge,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Твій голос — твоя сила",
                    style = AppTypography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }

            // Features Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Покращ свою дикцію, інтонацію та впевненість у мовленні з AI-тренером",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                FeatureItem(
                    icon = Icons.Default.Assessment,
                    text = "Персоналізована діагностика"
                )

                FeatureItem(
                    icon = Icons.Default.FitnessCenter,
                    text = "Щоденні розминки для голосу"
                )

                FeatureItem(
                    icon = Icons.Default.MenuBook,
                    text = "Тематичні курси"
                )

                FeatureItem(
                    icon = Icons.Default.Assistant,
                    text = "AI-тренер для персональних порад"
                )

                FeatureItem(
                    icon = Icons.Default.TrendingUp,
                    text = "Відстеження прогресу"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        text = "Почати →",
                        style = AppTypography.titleMedium,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                PageIndicator(currentPage = 0, totalPages = 4)
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF667EEA),
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

package com.aivoicepower.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit = {}
) {
    GradientBackground(content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top bar with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = Color.Black.copy(alpha = 0.08f)
                        )
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color(0xFF667EEA),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Про застосунок",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Logo & Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667EEA),
                                    Color(0xFF764BA2),
                                    Color(0xFF9333EA)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎤", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "AI VoicePower",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Версія 1.0.0",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Description Card
            InfoCard(
                title = "Про застосунок",
                description = "AI VoicePower — це інноваційний застосунок для розвитку " +
                        "навичок публічних виступів та комунікації з використанням " +
                        "штучного інтелекту. Ми допомагаємо вам стати впевненішими " +
                        "ораторами через персоналізовані тренування та AI-аналіз."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Features
            InfoCard(
                title = "Основні можливості",
                content = {
                    FeatureItem(
                        icon = Icons.Default.School,
                        text = "Професійні курси для розвитку мовлення"
                    )
                    FeatureItem(
                        icon = Icons.Default.Mic,
                        text = "AI-аналіз голосу та дикції"
                    )
                    FeatureItem(
                        icon = Icons.Default.Psychology,
                        text = "Імпровізація та спонтанне мовлення"
                    )
                    FeatureItem(
                        icon = Icons.Default.SmartToy,
                        text = "Персональний AI-тренер"
                    )
                    FeatureItem(
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        text = "Детальна аналітика прогресу",
                        showDivider = false
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tech Stack
            InfoCard(
                title = "Технології",
                description = "Застосунок розроблено з використанням сучасних технологій: " +
                        "Kotlin, Jetpack Compose, Google Gemini AI, Firebase. " +
                        "Ми постійно працюємо над покращенням та додаванням нових функцій."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact
            InfoCard(
                title = "Контакти",
                content = {
                    ContactItem(
                        icon = Icons.Default.Email,
                        text = "support@aivoicepower.com"
                    )
                    ContactItem(
                        icon = Icons.Default.Language,
                        text = "www.aivoicepower.com",
                        showDivider = false
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Copyright
            Text(
                text = "© 2026 AI VoicePower\nВсі права захищені",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    })
}

@Composable
private fun InfoCard(
    title: String,
    description: String? = null,
    content: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )

        if (description != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 22.sp
            )
        }

        if (content != null) {
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    text: String,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Color(0xFF667EEA).copy(alpha = 0.1f),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF667EEA),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                color = Color(0xFF4B5563),
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 48.dp),
                color = Color(0xFFF3F4F6),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun ContactItem(
    icon: ImageVector,
    text: String,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF667EEA),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                color = Color(0xFF4B5563)
            )
        }

        if (showDivider) {
            HorizontalDivider(
                color = Color(0xFFF3F4F6),
                thickness = 1.dp
            )
        }
    }
}

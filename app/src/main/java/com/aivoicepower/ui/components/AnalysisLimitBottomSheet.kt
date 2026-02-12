package com.aivoicepower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
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
import com.aivoicepower.utils.constants.FreeTierLimits

data class AnalysisLimitInfo(
    val usedAnalyses: Int,
    val maxFreeAnalyses: Int,
    val remainingAdAnalyses: Int,
    val isAdLoaded: Boolean,
    val isImprovisation: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisLimitBottomSheet(
    limitInfo: AnalysisLimitInfo,
    onWatchAd: () -> Unit,
    onPremium: () -> Unit,
    onContinueWithout: () -> Unit,
    onDismiss: () -> Unit
) {
    val maxAnalyses = if (limitInfo.isImprovisation) {
        FreeTierLimits.FREE_IMPROV_ANALYSES_PER_DAY
    } else {
        FreeTierLimits.FREE_ANALYSES_PER_DAY
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFD1D5DB))
            )
        },
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
        ) {
            // Inner content with blue border
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mic icon (like Paywall)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = CircleShape,
                            spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667EEA),
                                    Color(0xFF764BA2),
                                    Color(0xFF9333EA)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = "Аналізи на сьогодні вичерпано",
                    color = Color(0xFF1F2937),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Usage indicator
                Text(
                    text = "Використано $maxAnalyses/$maxAnalyses безкоштовних аналізів",
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                // Progress bar
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFEF4444),
                    trackColor = Color(0xFFE5E7EB)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Premium CTA — primary (anchoring: show value first)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onPremium() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Отримати PRO — необмежені аналізи",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Детальний AI фідбек для кожної вправи \u2022 від \$9.99/міс",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Watch Ad — secondary
                if (limitInfo.remainingAdAnalyses > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color(0xFFD1D5DB),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(enabled = limitInfo.isAdLoaded) { onWatchAd() }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "\u25B6\uFE0F", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (limitInfo.isAdLoaded)
                                    "Переглянути рекламу \u2022 +1 аналіз (${limitInfo.remainingAdAnalyses} залишилось)"
                                else "Реклама завантажується...",
                                color = if (limitInfo.isAdLoaded) Color(0xFF374151) else Color(0xFF9CA3AF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Continue without — tertiary text button
                Text(
                    text = "Продовжити без аналізу",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onContinueWithout() }
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Social proof / scarcity
                Text(
                    text = "\uD83D\uDD25 4,200+ користувачів вже обрали PRO",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Simplified card for results without analysis
 */
@Composable
fun NoAnalysisResultCard(
    recordingDurationMs: Long,
    onWatchAd: (() -> Unit)? = null,
    onPremium: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\u2705 Запис збережено!",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        val seconds = (recordingDurationMs / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        Text(
            text = "\u23F1 Тривалість: ${minutes}:${String.format("%02d", remainingSeconds)}",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "\uD83D\uDCCA Аналіз недоступний",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "\uD83D\uDCA1 Порада: Прослухайте свій запис самостійно\nта порівняйте з оригіналом",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        if (onWatchAd != null || onPremium != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\uD83D\uDD13 Розблокуйте аналіз: ",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                if (onWatchAd != null) {
                    Text(
                        text = "Реклама",
                        color = Color(0xFF22C55E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onWatchAd() }
                    )
                    if (onPremium != null) {
                        Text(
                            text = " | ",
                            color = Color.White.copy(alpha = 0.3f),
                            fontSize = 12.sp
                        )
                    }
                }
                if (onPremium != null) {
                    Text(
                        text = "Premium",
                        color = Color(0xFF8B5CF6),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onPremium() }
                    )
                }
            }
        }
    }
}

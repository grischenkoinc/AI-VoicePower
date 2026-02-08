package com.aivoicepower.ui.screens.progress.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.utils.SkillLevelUtils

private data class JourneyMilestone(
    val threshold: Int,
    val label: String,
    val emoji: String,
    val color: Color
)

private val milestones = listOf(
    JourneyMilestone(0, "Новачок", "\uD83C\uDF31", Color(0xFF9E9E9E)),
    JourneyMilestone(21, "Учень", "\uD83D\uDCDA", Color(0xFF4CAF50)),
    JourneyMilestone(41, "Практик", "\uD83C\uDFAF", Color(0xFF2196F3)),
    JourneyMilestone(61, "Впевнений", "\uD83D\uDCAA", Color(0xFF9C27B0)),
    JourneyMilestone(81, "Майстер", "⭐", Color(0xFFFF9800)),
    JourneyMilestone(91, "Експерт", "\uD83D\uDC51", Color(0xFFFFD700))
)

@Composable
fun JourneySection(
    overallLevel: Int,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animationStarted = true }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Ваша подорож",
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )

        // Current rank display
        val currentLabel = SkillLevelUtils.getSkillLabel(overallLevel)
        val currentColor = SkillLevelUtils.getSkillLabelColor(overallLevel)
        val currentMilestone = milestones.lastOrNull { overallLevel >= it.threshold } ?: milestones.first()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            currentColor.copy(alpha = 0.1f),
                            currentColor.copy(alpha = 0.05f)
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = currentMilestone.emoji,
                fontSize = 36.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Зараз ви — $currentLabel",
                    style = AppTypography.titleMedium,
                    color = currentColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Загальний рівень: $overallLevel",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Timeline
        milestones.forEachIndexed { index, milestone ->
            val isReached = overallLevel >= milestone.threshold
            val isCurrent = overallLevel >= milestone.threshold &&
                    (index == milestones.lastIndex || overallLevel < milestones[index + 1].threshold)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Node
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .then(
                            if (isCurrent) Modifier.shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                spotColor = milestone.color.copy(alpha = 0.5f)
                            ) else Modifier
                        )
                        .background(
                            if (isReached) milestone.color else Color(0xFFE5E7EB),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isReached) {
                        Text(
                            text = if (isCurrent) milestone.emoji else "✓",
                            fontSize = if (isCurrent) 18.sp else 16.sp,
                            color = Color.White
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Label
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = milestone.label,
                        style = AppTypography.bodyLarge,
                        color = if (isReached) TextColors.onLightPrimary else TextColors.onLightMuted,
                        fontSize = 15.sp,
                        fontWeight = if (isCurrent) FontWeight.ExtraBold else if (isReached) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                        text = if (milestone.threshold == 0) "Рівень 1-20" else "Рівень ${milestone.threshold}+",
                        style = AppTypography.labelSmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 11.sp
                    )
                }

                // Status
                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .background(
                                milestone.color.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Зараз тут",
                            style = AppTypography.labelSmall,
                            color = milestone.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (isReached) {
                    Text(
                        text = "✓",
                        color = milestone.color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Connector line
            if (index < milestones.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(start = 17.dp)
                        .width(2.dp)
                        .height(20.dp)
                        .background(
                            if (overallLevel >= milestones[index + 1].threshold) milestones[index + 1].color
                            else Color(0xFFE5E7EB)
                        )
                )
            }
        }

        // Next goal
        val nextMilestone = milestones.firstOrNull { overallLevel < it.threshold }
        if (nextMilestone != null) {
            val pointsNeeded = nextMilestone.threshold - overallLevel

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF0F9FF),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "\uD83C\uDFAF", fontSize = 20.sp)
                Column {
                    Text(
                        text = "Наступна ціль: ${nextMilestone.label}",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Залишилось $pointsNeeded балів",
                        style = AppTypography.bodySmall,
                        color = Color(0xFF0284C7),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

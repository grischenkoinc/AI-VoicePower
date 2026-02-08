package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.model.user.toDisplayString
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.utils.SkillLevelUtils

@Composable
fun SkillBarChart(
    skillLevels: Map<SkillType, Int>,
    onSkillClick: (SkillType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        skillLevels.forEach { (skill, level) ->
            SkillBar(
                skillType = skill,
                name = skill.toDisplayString(),
                level = level,
                onClick = { onSkillClick(skill) }
            )
        }
    }
}

@Composable
private fun SkillBar(
    skillType: SkillType,
    name: String,
    level: Int,
    onClick: () -> Unit
) {
    val barColors = getGradientColorsForLevel(level)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$level",
                    style = AppTypography.bodyMedium,
                    color = barColors.first,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = SkillLevelUtils.getSkillLabel(level),
                    style = AppTypography.bodyMedium,
                    color = SkillLevelUtils.getSkillLabelColor(level),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3D Progress bar with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
        ) {
            // Background track with subtle shadow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(7.dp),
                        spotColor = Color.Black.copy(alpha = 0.15f)
                    )
                    .background(
                        Color(0xFFE5E7EB),
                        RoundedCornerShape(7.dp)
                    )
            )

            // Gradient progress bar with 3D effect
            Box(
                modifier = Modifier
                    .fillMaxWidth(level / 100f)
                    .height(14.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(7.dp),
                        spotColor = barColors.first.copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(barColors.first, barColors.second)
                        ),
                        RoundedCornerShape(7.dp)
                    )
            )
        }
    }
}

/**
 * Повертає пару кольорів для градієнта залежно від рівня:
 * 1 = червоний, 50 = жовтий, 99 = зелений
 * Плавний перехід між кольорами
 */
private fun getGradientColorsForLevel(level: Int): Pair<Color, Color> {
    val normalizedLevel = level.coerceIn(1, 99) / 99f

    return when {
        level < 50 -> {
            // Червоний → Жовтий (1-49)
            val progress = level / 50f
            val red = 1f
            val green = progress

            Pair(
                Color(red = red, green = green, blue = 0f),
                Color(red = red * 0.85f, green = green * 0.85f, blue = 0f)
            )
        }
        else -> {
            // Жовтий → Зелений (50-99)
            val progress = (level - 50f) / 49f
            val red = 1f - progress
            val green = 1f

            Pair(
                Color(red = red, green = green, blue = 0f),
                Color(red = red * 0.85f, green = green * 0.85f, blue = 0f)
            )
        }
    }
}

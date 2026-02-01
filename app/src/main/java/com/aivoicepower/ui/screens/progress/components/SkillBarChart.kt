package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.model.user.toDisplayString
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun SkillBarChart(
    skillLevels: Map<SkillType, Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        skillLevels.forEach { (skill, level) ->
            SkillBar(
                name = skill.toDisplayString(),
                level = level
            )
        }
    }
}

@Composable
private fun SkillBar(
    name: String,
    level: Int
) {
    val barColor = getColorForLevel(level)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$level",
                style = AppTypography.bodyMedium,
                color = barColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { level / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = barColor,
            trackColor = Color(0xFFE5E7EB)
        )
    }
}

/**
 * Повертає колір залежно від рівня:
 * 1 = червоний, 99 = зелений, градієнт між ними
 */
private fun getColorForLevel(level: Int): Color {
    val normalizedLevel = level.coerceIn(1, 99) / 99f

    return when {
        normalizedLevel < 0.5f -> {
            // Червоний -> Жовтий (0-50)
            val progress = normalizedLevel * 2
            Color(
                red = 1f,
                green = progress,
                blue = 0f
            )
        }
        else -> {
            // Жовтий -> Зелений (50-99)
            val progress = (normalizedLevel - 0.5f) * 2
            Color(
                red = 1f - progress,
                green = 1f,
                blue = 0f
            )
        }
    }
}

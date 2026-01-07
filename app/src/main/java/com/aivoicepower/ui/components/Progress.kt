package com.aivoicepower.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * Circular Progress - круговий індикатор прогресу
 */
@Composable
fun CircularProgress(
    progress: Float, // 0.0 - 1.0
    modifier: Modifier = Modifier,
    size: Int = 120, // dp
    strokeWidth: Int = 8, // dp
    showPercentage: Boolean = true
) {
    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Canvas(modifier = Modifier.size(size.dp)) {
            drawArc(
                color = BackgroundColors.primary.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Progress arc with gradient
        Canvas(modifier = Modifier.size(size.dp)) {
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(
                        PrimaryColors.default,
                        AccentColors.default
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Percentage text
        if (showPercentage) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = TextColors.primary
            )
        }
    }
}

/**
 * Streak Counter - лічильник днів поспіль
 */
@Composable
fun StreakCounter(
    streak: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = GlassEffect.backgroundMedium,
        border = BorderStroke(
            width = 1.dp,
            color = BorderColors.accent
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Spacing.md,
                vertical = Spacing.sm
            ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$streak",
                style = MaterialTheme.typography.titleMedium,
                color = TextColors.primary
            )
            Text(
                text = "днів",
                style = MaterialTheme.typography.labelSmall,
                color = TextColors.secondary
            )
        }
    }
}

/**
 * Skill Progress Bar - прогрес навички з підписом
 */
@Composable
fun SkillProgressBar(
    skillName: String,
    progress: Float, // 0.0 - 1.0
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = skillName,
                style = MaterialTheme.typography.bodyMedium,
                color = TextColors.primary
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = TextColors.secondary
            )
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = AccentColors.default,
            trackColor = BackgroundColors.primary.copy(alpha = 0.3f)
        )
    }
}

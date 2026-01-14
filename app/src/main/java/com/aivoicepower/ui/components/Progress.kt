package com.aivoicepower.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.modifiers.*

/**
 * Circular Progress - круговий індикатор прогресу
 * Розміри: 120.dp (великий), 80.dp (середній), 48.dp (малий)
 */
@Composable
fun CircularProgress(
    progress: Float, // 0.0 - 1.0
    modifier: Modifier = Modifier,
    size: Int = 120, // dp
    strokeWidth: Int = 12, // dp
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = AnimationDuration.long,
            easing = AnimationEasing.decelerate
        ),
        label = "circular_progress"
    )

    val sizeDp = size.dp
    val strokeWidthDp = when (size) {
        120 -> 12.dp
        80 -> 8.dp
        else -> 6.dp
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .then(
                if (animatedProgress >= 1f) {
                    Modifier.shadowPreset(ShadowPreset.CIRCULAR_PROGRESS, CircleShape)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            // Track
            drawArc(
                color = ProgressBarColors.trackStart,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidthDp.toPx(), cap = StrokeCap.Round)
            )

            // Progress (з градієнтом)
            drawArc(
                brush = Gradients.progressFill,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidthDp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Percentage text
        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = when (size) {
                    120 -> MaterialTheme.typography.headlineMedium
                    80 -> MaterialTheme.typography.titleLarge
                    else -> MaterialTheme.typography.bodyLarge
                },
                color = TextColors.primary
            )
        }
    }
}

/**
 * Streak Counter - лічильник днів поспіль з fire icon
 */
@Composable
fun StreakCounter(
    streak: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .glassEffect(GlassStrength.MEDIUM, RoundedCornerShape(CornerRadius.full))
            .border(1.dp, BorderColors.border, RoundedCornerShape(CornerRadius.full))
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Whatshot,
            contentDescription = "Streak",
            modifier = Modifier.size(20.dp),
            tint = AccentColors.default
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = streak.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = TextColors.primary
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = "днів",
            style = MaterialTheme.typography.labelSmall,
            color = TextColors.secondary
        )
    }
}

/**
 * Skill Progress Bar - 3D прогрес-бар з inset тінню та glow
 * Height: 14.dp з фіолетовим track та зеленим fill
 */
@Composable
fun SkillProgressBar(
    skillName: String,
    progress: Float, // 0.0 - 1.0
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = AnimationDuration.medium,
            easing = AnimationEasing.decelerate
        ),
        label = "progress_animation"
    )

    Column(modifier = modifier) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = skillName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextColors.secondary
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextColors.primary
                )
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
        }

        // Track with inset shadow effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .gradientBackground(Gradients.progressTrack, RoundedCornerShape(CornerRadius.full))
                // Inset shadow імітація через inner border
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ShadowColors.black20,
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(CornerRadius.full)
                )
        ) {
            // Progress fill with glow
            if (animatedProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .glowShadow(
                            glowRadius = 16.dp,
                            glowColor = Color(0xFF22C55E),
                            intensity = 0.7f
                        )
                        .gradientBackground(Gradients.progressFill, RoundedCornerShape(CornerRadius.full))
                )
            }
        }
    }
}

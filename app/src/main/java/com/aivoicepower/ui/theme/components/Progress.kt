package com.aivoicepower.ui.theme.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*

/**
 * AI VoicePower Progress Components v2.0
 *
 * Джерело: Design_Example_react.md
 * 3D Progress Bar з inset shadows, glow та highlights
 */

/**
 * 3D Progress Bar
 * CSS: .progress-indicator
 *
 * Повний компонент з track, fill та stats
 */
@Composable
fun ProgressBar3D(
    progress: Float,  // 0.0 to 1.0
    currentStep: Int,
    totalSteps: Int,
    stepLabel: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Progress Track + Fill
        ProgressTrack(progress = progress)

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepCounter(current = currentStep, total = totalSteps)

            Text(
                text = stepLabel,
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

/**
 * Progress Track з 3D ефектом
 * CSS: .progress-track
 *
 * Inset shadows + top highlight
 */
@Composable
private fun ProgressTrack(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(21.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Gradients.progressTrack)
    ) {
        // Deboss effect - inner shadow top (темна тінь зверху)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.TopCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Deboss effect - inner shadow bottom (світла тінь знизу)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        // Deboss effect - inner shadow left (темна тінь зліва)
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Deboss effect - inner shadow right (світла тінь справа)
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.3f)
                        )
                    )
                )
        )

        // Progress Fill
        ProgressFill(progress = progress)
    }
}

/**
 * Progress Fill з 3D ефектом та glow
 * CSS: .progress-fill
 *
 * Horizontal gradient + glow shadow + top highlight + edge shimmer
 */
@Composable
private fun ProgressFill(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = ProgressFillAnimation.duration,
            easing = ProgressFillAnimation.easing
        ),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
            .shadow(
                elevation = Elevation.ProgressGlow.blurRadius,
                shape = RoundedCornerShape(20.dp),
                spotColor = Elevation.ProgressGlow.color,
                clip = false
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Gradients.progressFill)
    ) {
        // Top highlight (білий градієнт зверху)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(
                    brush = Gradients.progressTopHighlight,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
        )

        // Edge shimmer (світлий край справа)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(50.dp)
                .fillMaxHeight()
                .background(Gradients.progressShimmer)
        )
    }
}

/**
 * Glass Circle (прогрес у колі)
 * CSS: .progress-circle
 *
 * Використовується для "1/4" тощо
 */
@Composable
fun GlassCircle(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = 50
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(GlassColors.background, CircleShape)
            .border(
                width = 2.dp,
                color = GlassColors.borderMedium,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.labelMedium,
            color = TextColors.onDarkPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

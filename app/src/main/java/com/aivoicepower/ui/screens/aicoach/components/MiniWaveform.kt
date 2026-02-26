package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.PrimaryColors

@Composable
fun MiniWaveform(
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    barWidth: Dp = 3.dp,
    maxBarHeight: Dp = 18.dp,
    minBarHeight: Dp = 4.dp,
    startColor: Color = PrimaryColors.light,
    endColor: Color = PrimaryColors.default
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            val phase = index * 120
            val heightFraction by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 800 + index * 100,
                        delayMillis = phase,
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            val barHeight = minBarHeight + (maxBarHeight - minBarHeight) * heightFraction

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(barHeight)
                    .clip(RoundedCornerShape(barWidth / 2))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(startColor, endColor)
                        )
                    )
            )
        }
    }
}

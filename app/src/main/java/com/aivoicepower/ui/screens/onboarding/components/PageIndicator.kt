package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Legacy static page indicator — kept for backward compatibility.
 * Use [AnimatedPageIndicator] for the onboarding pager.
 */
@Composable
fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    AnimatedPageIndicator(
        currentPage = currentPage,
        totalPages = totalPages,
        modifier = modifier
    )
}

/**
 * Animated page indicator with expanding pill-shaped active dot.
 *
 * Active dot widens to 24dp with gradient fill; inactive dots are 8dp circles.
 * Smoothly animates width and opacity transitions (250ms).
 */
@Composable
fun AnimatedPageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
    activeColor: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
    ),
    inactiveColor: Color = Color.White.copy(alpha = 0.35f)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalPages) { index ->
                val isActive = index == currentPage

                val width by animateDpAsState(
                    targetValue = if (isActive) 24.dp else 8.dp,
                    animationSpec = tween(250),
                    label = "dotWidth_$index"
                )
                val alpha by animateFloatAsState(
                    targetValue = if (isActive) 1f else 0.35f,
                    animationSpec = tween(250),
                    label = "dotAlpha_$index"
                )

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(RoundedCornerShape(4.dp))
                        .then(
                            if (isActive) {
                                Modifier.background(activeColor, RoundedCornerShape(4.dp))
                            } else {
                                Modifier.background(
                                    inactiveColor.copy(alpha = alpha),
                                    RoundedCornerShape(4.dp)
                                )
                            }
                        )
                )
            }
        }
    }
}

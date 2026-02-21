package com.aivoicepower.ui.theme.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.utils.performHaptic

/**
 * AI VoicePower Button Components v2.0
 *
 * Джерело: Design_Example_react.md
 */

/**
 * Record Button з wave rings
 * CSS: .record-btn + .wave-ring
 *
 * Idle: pulse animation
 * Recording: 3 wave rings
 */
@Composable
fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    // Pulse animation для idle стану
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = RecordButtonPulse.scaleFrom,
        targetValue = RecordButtonPulse.scaleTo,
        animationSpec = RecordButtonPulse.animationSpec(),
        label = "scale"
    )

    // Border animation для recording стану
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = if (isRecording) 0.3f else 0f,
        targetValue = if (isRecording) 0.8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )

    Box(
        modifier = modifier.size(140.dp), // Збільшено для border
        contentAlignment = Alignment.Center
    ) {
        // Animated border (тільки коли recording)
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 3.dp,
                        color = Color(0xFF667EEA).copy(alpha = borderAlpha),
                        shape = CircleShape
                    )
            )
        }

        // Wave rings (тільки коли recording)
        if (isRecording) {
            WaveRing(delay = WaveRingExpansion.delay1)
            WaveRing(delay = WaveRingExpansion.delay2)
            WaveRing(delay = WaveRingExpansion.delay3)
        }

        // Main button
        Box(
            modifier = Modifier
                .size(110.dp)
                .scale(if (!isRecording) scale else 1f)
                .shadow(
                    elevation = if (isRecording)
                        Elevation.RecordButton.activeElevation
                    else
                        Elevation.RecordButton.idleElevation,
                    shape = CircleShape,
                    spotColor = if (isRecording)
                        Elevation.RecordButton.activeColor
                    else
                        Elevation.RecordButton.idleColor
                )
                .background(Gradients.recordButton, CircleShape)
                .clickable { performHaptic(view); onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isRecording) "⏸️" else "🎤",
                fontSize = 44.sp
            )
        }
    }
}

/**
 * Wave Ring для RecordButton
 * CSS: @keyframes waveExpand
 */
@Composable
private fun WaveRing(
    delay: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val scale by infiniteTransition.animateFloat(
        initialValue = WaveRingExpansion.scaleFrom,
        targetValue = WaveRingExpansion.scaleTo,
        animationSpec = WaveRingExpansion.scaleAnimationSpec(delay),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = WaveRingExpansion.opacityFrom,
        targetValue = WaveRingExpansion.opacityTo,
        animationSpec = WaveRingExpansion.opacityAnimationSpec(delay),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(134.dp)
            .scale(scale)
            .border(
                width = 3.dp,
                color = Color(0xFF667EEA).copy(alpha = alpha * 0.5f),
                shape = CircleShape
            )
    )
}

/**
 * Navigation Button (Prev/Next)
 * CSS: .nav-btn
 */
@Composable
fun NavButton(
    text: String,
    icon: String = "",
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier
            .then(
                if (isPrimary) {
                    Modifier.shadow(
                        elevation = if (isPressed)
                            Elevation.NavButtonPrimary.elevation
                        else
                            Elevation.NavButtonPrimary.hoverElevation,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Elevation.NavButtonPrimary.color
                    )
                } else Modifier
            )
            .background(
                color = if (isPrimary) Color.White else GlassColors.background,
                shape = RoundedCornerShape(20.dp)
            )
            .then(
                if (!isPrimary) {
                    Modifier.border(
                        width = 2.dp,
                        color = GlassColors.borderLight,
                        shape = RoundedCornerShape(20.dp)
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { performHaptic(view); onClick() }
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon.isNotEmpty()) {
            Text(
                text = icon,
                fontSize = 15.sp
            )
        }
        Text(
            text = text.uppercase(),
            style = AppTypography.labelLarge,
            color = if (isPrimary) Color(0xFF667EEA) else TextColors.onDarkPrimary,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Primary Button з градієнтним фоном
 * Використовується для основних дій (Далі, Завершити, тощо)
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = { performHaptic(view); onClick() }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.titleMedium,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/**
 * Secondary Button з білим фоном та рамкою
 * Використовується для другорядних дій (Назад, Перезаписати, тощо)
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = { performHaptic(view); onClick() }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

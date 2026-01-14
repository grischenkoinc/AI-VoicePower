package com.aivoicepower.ui.theme.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Glass Modifiers v2.0
 *
 * Glassmorphism system для backdrop blur ефектів
 */

/**
 * Glass Background (standard glassmorphism)
 *
 * Використання:
 * Modifier.glassBackground(
 *     shape = RoundedCornerShape(20.dp),
 *     backgroundColor = Color.White.copy(alpha = 0.15f),
 *     borderColor = Color.White.copy(alpha = 0.3f)
 * )
 */
fun Modifier.glassBackground(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color.White.copy(alpha = 0.15f),
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    borderWidth: Dp = 1.5.dp,
    blurRadius: Dp = 0.dp // Blur не працює на Android, залишаємо для майбутнього
): Modifier = this.then(
    Modifier
        .clip(shape)
        .background(backgroundColor, shape)
        .border(borderWidth, borderColor, shape)
)

/**
 * Frost Glass (heavy blur, less transparent)
 *
 * Використання:
 * Modifier.frostGlass(
 *     shape = RoundedCornerShape(16.dp)
 * )
 */
fun Modifier.frostGlass(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Color.White.copy(alpha = 0.25f),
    borderColor: Color = Color.White.copy(alpha = 0.4f),
    borderWidth: Dp = 1.5.dp
): Modifier = this.then(
    Modifier
        .clip(shape)
        .background(backgroundColor, shape)
        .border(borderWidth, borderColor, shape)
)

/**
 * Light Glass (subtle, minimal blur)
 *
 * Використання:
 * Modifier.lightGlass(
 *     shape = RoundedCornerShape(12.dp)
 * )
 */
fun Modifier.lightGlass(
    shape: Shape = RoundedCornerShape(12.dp),
    backgroundColor: Color = Color.White.copy(alpha = 0.08f),
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    borderWidth: Dp = 1.dp
): Modifier = this.then(
    Modifier
        .clip(shape)
        .background(backgroundColor, shape)
        .border(borderWidth, borderColor, shape)
)

/**
 * Dark Glass (for modals, overlays)
 *
 * Використання:
 * Modifier.darkGlass(
 *     shape = RoundedCornerShape(24.dp)
 * )
 */
fun Modifier.darkGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color.Black.copy(alpha = 0.4f),
    borderColor: Color = Color.White.copy(alpha = 0.15f),
    borderWidth: Dp = 1.dp
): Modifier = this.then(
    Modifier
        .clip(shape)
        .background(backgroundColor, shape)
        .border(borderWidth, borderColor, shape)
)

/**
 * Gradient Glass (з gradient background)
 *
 * Використання:
 * Modifier.gradientGlass(
 *     gradient = Brush.linearGradient(
 *         colors = listOf(
 *             Color.White.copy(alpha = 0.2f),
 *             Color.White.copy(alpha = 0.1f)
 *         )
 *     ),
 *     shape = RoundedCornerShape(20.dp)
 * )
 */
fun Modifier.gradientGlass(
    gradient: Brush,
    shape: Shape = RoundedCornerShape(20.dp),
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    borderWidth: Dp = 1.5.dp
): Modifier = this.then(
    Modifier
        .clip(shape)
        .background(gradient, shape)
        .border(borderWidth, borderColor, shape)
)

/**
 * Tinted Glass (з кольоровим відтінком)
 *
 * Використання:
 * Modifier.tintedGlass(
 *     tintColor = Color(0xFFFBBF24),
 *     shape = RoundedCornerShape(16.dp)
 * )
 */
fun Modifier.tintedGlass(
    tintColor: Color,
    tintAlpha: Float = 0.15f,
    shape: Shape = RoundedCornerShape(16.dp),
    borderColor: Color = tintColor.copy(alpha = 0.3f),
    borderWidth: Dp = 1.5.dp
): Modifier = this.then(
    Modifier
        .clip(shape)
        .background(tintColor.copy(alpha = tintAlpha), shape)
        .border(borderWidth, borderColor, shape)
)

/**
 * Frosted Bottom Bar (для navigation)
 *
 * Використання:
 * Modifier.frostedBottomBar()
 */
fun Modifier.frostedBottomBar(
    backgroundColor: Color = Color(0xF50A0A0F), // 96% opacity dark
    borderColor: Color = Color.White.copy(alpha = 0.1f),
    borderWidth: Dp = 1.dp
): Modifier = this.then(
    Modifier
        .background(backgroundColor)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
        )
)

/**
 * Frosted Top Bar (для headers)
 *
 * Використання:
 * Modifier.frostedTopBar()
 */
fun Modifier.frostedTopBar(
    backgroundColor: Color = Color.White.copy(alpha = 0.9f),
    borderColor: Color = Color.Black.copy(alpha = 0.1f),
    borderWidth: Dp = 1.dp
): Modifier = this.then(
    Modifier
        .background(backgroundColor)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
        )
)

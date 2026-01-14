package com.aivoicepower.ui.theme.modifiers

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Animation Modifiers v2.0
 *
 * Reusable animation utilities
 */

/**
 * Shimmer Effect (loading state)
 *
 * Використання:
 * Modifier.shimmerEffect()
 */
fun Modifier.shimmerEffect(
    colors: List<Color> = listOf(
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f)
    ),
    duration: Int = 1500
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    this.drawWithContent {
        drawContent()

        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset(translateX, 0f),
            end = Offset(translateX + 200f, 200f)
        )

        drawRect(brush = brush)
    }
}

/**
 * Pulse Animation (attention grabbing)
 *
 * Використання:
 * Modifier.pulseAnimation(
 *     scaleFrom = 1f,
 *     scaleTo = 1.05f
 * )
 */
fun Modifier.pulseAnimation(
    scaleFrom: Float = 1f,
    scaleTo: Float = 1.05f,
    duration: Int = 1500,
    easing: Easing = FastOutSlowInEasing
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = scaleFrom,
        targetValue = scaleTo,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = easing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    this.scale(scale)
}

/**
 * Shake Animation (error state)
 *
 * Використання:
 * val shakeController = remember { ShakeController() }
 * Modifier.shakeAnimation(shakeController)
 *
 * // Trigger shake:
 * shakeController.shake()
 */
class ShakeController {
    private val _shakeOffset = Animatable(0f)
    val shakeOffset: Float
        get() = _shakeOffset.value

    suspend fun shake(
        strength: Float = 10f,
        duration: Int = 300
    ) {
        val keyframes = keyframes<Float> {
            durationMillis = duration
            0f at 0
            strength at duration / 8
            -strength at duration / 4
            strength at duration * 3 / 8
            -strength at duration / 2
            strength at duration * 5 / 8
            -strength at duration * 3 / 4
            0f at duration
        }
        _shakeOffset.animateTo(0f, keyframes)
    }
}

fun Modifier.shakeAnimation(
    controller: ShakeController
): Modifier = composed {
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(controller.shakeOffset.dp.roundToPx(), 0)
        }
    }
}

/**
 * Scale On Press (button interaction)
 *
 * Використання:
 * Modifier.scaleOnPress(
 *     pressedScale = 0.95f
 * )
 */
fun Modifier.scaleOnPress(
    pressedScale: Float = 0.95f,
    duration: Int = 150
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "scale"
    )

    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

/**
 * Bounce Animation (playful interaction)
 *
 * Використання:
 * Modifier.bounceAnimation(
 *     enabled = triggerBounce
 * )
 */
fun Modifier.bounceAnimation(
    enabled: Boolean,
    bounceHeight: Dp = 20.dp,
    duration: Int = 600
): Modifier = composed {
    val offsetY by animateDpAsState(
        targetValue = if (enabled) -bounceHeight else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce"
    )

    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(0, offsetY.roundToPx())
        }
    }
}

/**
 * Rotate Animation (spinning loader)
 *
 * Використання:
 * Modifier.rotateAnimation()
 */
fun Modifier.rotateAnimation(
    duration: Int = 1000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    this.drawWithContent {
        rotate(rotation) {
            this@drawWithContent.drawContent()
        }
    }
}

/**
 * Fade In Animation
 *
 * Використання:
 * Modifier.fadeIn(visible = isVisible)
 */
fun Modifier.fadeIn(
    visible: Boolean,
    duration: Int = 300
): Modifier = composed {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(duration),
        label = "alpha"
    )

    this.drawWithContent {
        drawContent()
        drawRect(Color.White.copy(alpha = 1f - alpha))
    }
}

/**
 * Slide From Bottom Animation
 *
 * Використання:
 * Modifier.slideFromBottom(visible = isVisible)
 */
fun Modifier.slideFromBottom(
    visible: Boolean,
    slideDistance: Dp = 50.dp,
    duration: Int = 400
): Modifier = composed {
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else slideDistance,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "slide"
    )

    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(0, offsetY.roundToPx())
        }
    }
}

/**
 * Slide From Top Animation
 *
 * Використання:
 * Modifier.slideFromTop(visible = isVisible)
 */
fun Modifier.slideFromTop(
    visible: Boolean,
    slideDistance: Dp = 50.dp,
    duration: Int = 400
): Modifier = composed {
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else -slideDistance,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "slide"
    )

    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(0, offsetY.roundToPx())
        }
    }
}

/**
 * Expand/Collapse Animation
 *
 * Використання:
 * Modifier.expandCollapse(expanded = isExpanded)
 */
fun Modifier.expandCollapse(
    expanded: Boolean,
    duration: Int = 300
): Modifier = composed {
    val heightMultiplier by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "expand"
    )

    this.scale(scaleY = heightMultiplier, scaleX = 1f)
}

/**
 * Glow Pulse Animation (для active elements)
 *
 * Використання:
 * Modifier.glowPulse(
 *     color = Color(0xFF22C55E)
 * )
 */
fun Modifier.glowPulse(
    color: Color,
    minIntensity: Float = 0.3f,
    maxIntensity: Float = 0.7f,
    duration: Int = 1500
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val intensity by infiniteTransition.animateFloat(
        initialValue = minIntensity,
        targetValue = maxIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "intensity"
    )

    this.glowShadow(
        glowRadius = 16.dp,
        glowColor = color,
        intensity = intensity
    )
}

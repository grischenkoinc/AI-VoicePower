package com.aivoicepower.ui.theme

import androidx.compose.animation.core.*

/**
 * AI VoicePower Animation System v2.0
 *
 * Джерело: Design_Example_react.md
 * Timing та easing для всіх анімацій
 */

object AnimationDuration {
    // Micro-interactions
    const val instant = 100
    const val fast = 150
    const val normal = 200
    const val medium = 300
    const val slow = 400

    // Specific animations (з CSS)
    const val recordPulse = 1500      // Record button idle pulse
    const val recordActive = 1500     // Record button active state
    const val waveExpand = 2500       // Wave rings expansion
    const val borderPulse = 2000      // Card border pulse
    const val progressFill = 600      // Progress bar fill
    const val tipHover = 300          // Tip row hover
    const val buttonHover = 300       // Button hover

    // Compatibility aliases
    const val micro = instant
    const val short = normal
    const val long = slow
    const val emphasis = 800
}

object AnimationEasing {
    // Standard Material easing
    val standard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)
    val accelerate = CubicBezierEasing(0.4f, 0.0f, 1f, 1f)
    val decelerate = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1f)

    // Custom easing (CSS equivalents)
    val easeInOut = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)  // ease-in-out
    val easeOut = CubicBezierEasing(0.0f, 0.0f, 0.58f, 1.0f)     // ease-out
    val easeIn = CubicBezierEasing(0.42f, 0.0f, 1.0f, 1.0f)      // ease-in

    // Bouncy effects
    val bouncy = CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f)

    // Smooth
    val smooth = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)

    // Compatibility aliases
    val snappy = standard
}

/**
 * Record Button Pulse Animation
 * CSS: animation: pulse 1.5s ease-in-out infinite
 * Scale: 1.0 → 1.05
 */
object RecordButtonPulse {
    val duration = AnimationDuration.recordPulse
    val easing = AnimationEasing.easeInOut

    fun animationSpec() = infiniteRepeatable<Float>(
        animation = tween(
            durationMillis = duration,
            easing = easing
        ),
        repeatMode = RepeatMode.Reverse
    )

    // Scales
    const val scaleFrom = 1.0f
    const val scaleTo = 1.05f
}

/**
 * Record Button Active (тінь pulse)
 * CSS: Зміна shadow з 20dp → 25dp
 */
object RecordButtonActive {
    val duration = AnimationDuration.recordActive
    val easing = AnimationEasing.easeInOut

    fun animationSpec() = infiniteRepeatable<Float>(
        animation = tween(
            durationMillis = duration,
            easing = easing
        ),
        repeatMode = RepeatMode.Reverse
    )
}

/**
 * Wave Ring Expansion
 * CSS: animation: waveExpand 2.5s ease-out infinite
 * Scale: 1.0 → 1.7
 * Opacity: 1.0 → 0.0
 */
object WaveRingExpansion {
    val duration = AnimationDuration.waveExpand
    val easing = AnimationEasing.easeOut

    // Delays для 3 rings
    const val delay1 = 0
    const val delay2 = 800
    const val delay3 = 1600

    fun scaleAnimationSpec(delay: Int) = infiniteRepeatable<Float>(
        animation = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        ),
        repeatMode = RepeatMode.Restart
    )

    fun opacityAnimationSpec(delay: Int) = infiniteRepeatable<Float>(
        animation = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        ),
        repeatMode = RepeatMode.Restart
    )

    // Values
    const val scaleFrom = 1.0f
    const val scaleTo = 1.7f
    const val opacityFrom = 1.0f
    const val opacityTo = 0.0f
}

/**
 * Card Border Pulse (recording state)
 * CSS: animation: borderPulse 2s ease-in-out infinite
 */
object CardBorderPulse {
    val duration = AnimationDuration.borderPulse
    val easing = AnimationEasing.easeInOut

    fun animationSpec() = infiniteRepeatable<Float>(
        animation = tween(
            durationMillis = duration,
            easing = easing
        ),
        repeatMode = RepeatMode.Reverse
    )
}

/**
 * Progress Fill Animation
 * CSS: transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1)
 */
object ProgressFillAnimation {
    val duration = AnimationDuration.progressFill
    val easing = AnimationEasing.standard

    fun animationSpec() = tween<Float>(
        durationMillis = duration,
        easing = easing
    )
}

/**
 * Tip Row Hover
 * CSS: transition: transform 0.3s, box-shadow 0.3s
 * Transform: translateX(0) → translateX(6px)
 */
object TipRowHover {
    val duration = AnimationDuration.tipHover
    val easing = AnimationEasing.standard

    fun animationSpec() = tween<Float>(
        durationMillis = duration,
        easing = easing
    )

    const val translateFrom = 0f
    const val translateTo = 6f  // dp
}

/**
 * Button Hover (Nav buttons)
 * CSS: transition: all 0.3s
 * Transform: translateY(0) → translateY(-2px)
 * Shadow: збільшується
 */
object ButtonHover {
    val duration = AnimationDuration.buttonHover
    val easing = AnimationEasing.standard

    fun animationSpec() = tween<Float>(
        durationMillis = duration,
        easing = easing
    )

    const val translateY = -2f  // dp
}

/**
 * Stagger Delays
 * Для списків, grid тощо
 */
object StaggerDelays {
    const val listItem = 50
    const val gridItem = 100
    const val card = 150
}

// ===== COMPATIBILITY LAYER =====

object AnimationStagger {
    const val listItem = StaggerDelays.listItem
    const val gridItem = StaggerDelays.gridItem
    const val achievementSequence = 200
}

package com.aivoicepower.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/**
 * AI VoicePower Animation System
 * Based on Design Bible v1.0
 * 
 * Консистентні параметри анімацій для преміум UX
 */

// ============================================
// ANIMATION DURATION (в мілісекундах)
// ============================================
object AnimationDuration {
    const val micro = 100       // Зміна кольору, opacity
    const val short = 200       // Кнопки, chips, toggle
    const val medium = 350      // Картки, expand/collapse
    const val long = 500        // Переходи між екранами
    const val emphasis = 800    // Святкування, досягнення
}

// ============================================
// EASING CURVES
// ============================================
object AnimationEasing {
    /**
     * Standard easing — для більшості анімацій
     * Використання: Загальні transitions
     */
    val standard: Easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    
    /**
     * Decelerate — сповільнення в кінці
     * Використання: Елементи що з'являються (fade in, slide in)
     */
    val decelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    
    /**
     * Accelerate — прискорення в кінці
     * Використання: Елементи що зникають (fade out, slide out)
     */
    val accelerate: Easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    
    /**
     * Bouncy — відскік в кінці
     * Використання: Кнопки release, досягнення, playful interactions
     */
    val bouncy: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)
    
    /**
     * Smooth — плавна крива
     * Використання: Тривалі анімації, loops, pulse effects
     */
    val smooth: Easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
    
    /**
     * Snappy — різке закінчення
     * Використання: Button press, immediate feedback
     */
    val snappy: Easing = CubicBezierEasing(0.5f, 0.0f, 0.1f, 1.0f)
}

/**
 * Таблиця використання анімацій:
 * 
 * === DURATION ===
 * MICRO (100ms)    - Зміна кольору, opacity, hover states
 * SHORT (200ms)    - Кнопки, chips, toggle, checkbox
 * MEDIUM (350ms)   - Картки, expand/collapse, dialogs
 * LONG (500ms)     - Переходи між екранами, bottom sheets
 * EMPHASIS (800ms) - Святкування, досягнення, конфетті
 * 
 * === EASING ===
 * STANDARD         - Загальні transitions (за замовчуванням)
 * DECELERATE       - Елементи що з'являються (fade in, slide in)
 * ACCELERATE       - Елементи що зникають (fade out, slide out)
 * BOUNCY           - Playful interactions (button release, badges)
 * SMOOTH           - Тривалі анімації (progress, pulse, loops)
 * SNAPPY           - Immediate feedback (button press)
 * 
 * === КОМБІНАЦІЇ ===
 * Button Press:     duration=100ms, easing=snappy
 * Button Release:   duration=150ms, easing=bouncy
 * Card Appear:      duration=250ms, easing=decelerate
 * Card Disappear:   duration=250ms, easing=accelerate
 * Modal Open:       duration=350ms, easing=decelerate
 * Achievement:      duration=800ms, easing=bouncy
 * Progress Fill:    duration=400ms, easing=smooth
 */

// ============================================
// STAGGER DELAYS
// ============================================
object AnimationStagger {
    /**
     * Затримка між елементами списку при появі
     */
    const val listItem = 50  // мс між елементами
    
    /**
     * Затримка між елементами grid при появі
     */
    const val gridItem = 75  // мс між елементами
    
    /**
     * Затримка для achievement unlock sequence
     */
    const val achievementSequence = 200  // мс між кроками
}

/**
 * ПРИКЛАДИ ВИКОРИСТАННЯ:
 * 
 * ```kotlin
 * // Button press
 * val scale by animateFloatAsState(
 *     targetValue = if (pressed) 0.96f else 1f,
 *     animationSpec = tween(
 *         durationMillis = AnimationDuration.micro,
 *         easing = AnimationEasing.snappy
 *     )
 * )
 * 
 * // Card appear in list
 * LazyColumn {
 *     itemsIndexed(items) { index, item ->
 *         CardItem(
 *             modifier = Modifier.animateEnterExit(
 *                 enter = fadeIn(
 *                     animationSpec = tween(
 *                         durationMillis = AnimationDuration.short,
 *                         delayMillis = index * AnimationStagger.listItem,
 *                         easing = AnimationEasing.decelerate
 *                     )
 *                 ) + slideInVertically(
 *                     animationSpec = tween(
 *                         durationMillis = AnimationDuration.medium,
 *                         delayMillis = index * AnimationStagger.listItem,
 *                         easing = AnimationEasing.decelerate
 *                     ),
 *                     initialOffsetY = { it / 4 }
 *                 )
 *             )
 *         )
 *     }
 * }
 * 
 * // Achievement unlock
 * LaunchedEffect(showAchievement) {
 *     delay(AnimationStagger.achievementSequence.toLong())
 *     animateBadgeScale.animateTo(1.2f)
 *     delay(200)
 *     animateBadgeScale.animateTo(1f)
 * }
 * ```
 */

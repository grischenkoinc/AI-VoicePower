package com.aivoicepower.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Custom Modifiers
 * 
 * Extension functions для спрощення роботи з Design System
 */

// ============================================
// GLASSMORPHISM EFFECT
// ============================================

/**
 * Застосовує glass effect (glassmorphism)
 * 
 * @param strength Інтенсивність ефекту (LIGHT, MEDIUM, STRONG)
 * @param shape Форма елемента
 * @param border Чи додавати border
 * 
 * Приклад:
 * ```kotlin
 * Box(
 *     modifier = Modifier
 *         .size(200.dp)
 *         .glassEffect(GlassStrength.MEDIUM, RoundedCornerShape(16.dp))
 * )
 * ```
 */
fun Modifier.glassEffect(
    strength: GlassStrength = GlassStrength.MEDIUM,
    shape: Shape = RectangleShape,
    border: Boolean = true
): Modifier = this.composed {
    val backgroundColor = when (strength) {
        GlassStrength.LIGHT -> GlassEffect.backgroundLight
        GlassStrength.MEDIUM -> GlassEffect.backgroundMedium
        GlassStrength.STRONG -> GlassEffect.backgroundStrong
    }
    
    var modifier = this
        .background(backgroundColor, shape)
        .clip(shape)
    
    if (border) {
        modifier = modifier.border(
            width = 1.dp,
            color = GlassEffect.borderColor,
            shape = shape
        )
    }
    
    modifier
}

enum class GlassStrength {
    LIGHT,   // 5% white
    MEDIUM,  // 8% white
    STRONG   // 10% white
}

// ============================================
// GRADIENT BACKGROUND
// ============================================

/**
 * Застосовує gradient background
 * 
 * @param gradient Brush gradient з Gradients object
 * @param shape Форма елемента
 * 
 * Приклад:
 * ```kotlin
 * Button(
 *     onClick = { },
 *     modifier = Modifier.gradientBackground(Gradients.primary, RoundedCornerShape(12.dp))
 * )
 * ```
 */
fun Modifier.gradientBackground(
    gradient: Brush,
    shape: Shape = RectangleShape
): Modifier = this
    .background(gradient, shape)
    .clip(shape)

// ============================================
// SHADOW WITH PRESET
// ============================================

/**
 * Застосовує тінь з preset з Design System
 * 
 * ПРИМІТКА: Це спрощена версія, яка використовує стандартний Modifier.shadow()
 * Для справжніх multi-layer shadows потрібна складніша реалізація з Canvas
 * 
 * @param preset Тип тіні (CARD, ELEVATED, BUTTON_PRIMARY, тощо)
 * @param shape Форма елемента
 * 
 * Приклад:
 * ```kotlin
 * Card(
 *     modifier = Modifier.shadowPreset(ShadowPreset.CARD, RoundedCornerShape(16.dp))
 * )
 * ```
 */
fun Modifier.shadowPreset(
    preset: ShadowPreset,
    shape: Shape = RectangleShape
): Modifier = this.composed {
    val elevation = when (preset) {
        ShadowPreset.NONE -> 0.dp
        ShadowPreset.SUBTLE -> Elevation.level1
        ShadowPreset.CARD -> Elevation.level2
        ShadowPreset.ELEVATED -> Elevation.level3
        ShadowPreset.BUTTON_PRIMARY -> Elevation.level2
        ShadowPreset.BUTTON_CTA -> Elevation.level2
        ShadowPreset.FAB -> Elevation.level3
        ShadowPreset.MODAL -> Elevation.level5
    }
    
    // TODO: Для справжніх multi-layer shadows використовувати drawBehind
    // Поки що використовуємо стандартний shadow
    this.shadow(
        elevation = elevation,
        shape = shape,
        clip = false
    )
}

enum class ShadowPreset {
    NONE,            // Без тіні
    SUBTLE,          // Тонка тінь (chips, tags)
    CARD,            // Звичайна картка
    ELEVATED,        // Піднята картка/FAB
    BUTTON_PRIMARY,  // Primary кнопка
    BUTTON_CTA,      // CTA кнопка
    FAB,             // Floating action button
    MODAL            // Модальне вікно
}

// ============================================
// SPACING SHORTCUTS
// ============================================

/**
 * Extension для швидкого доступу до spacing
 */
fun Modifier.screenPadding(): Modifier = this.padding(horizontal = Spacing.screenHorizontal)

fun Modifier.cardPadding(): Modifier = this.padding(Spacing.cardPadding)

fun Modifier.sectionSpacing(): Modifier = this.padding(vertical = Spacing.sectionSpacing)

// ============================================
// CORNER RADIUS SHORTCUTS
// ============================================

/**
 * Extension для швидкого clip з corner radius
 */
fun Modifier.cornerRadius(radius: Dp): Modifier = this
    .clip(androidx.compose.foundation.shape.RoundedCornerShape(radius))

fun Modifier.cornerRadiusXs(): Modifier = this
    .clip(androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.xs))

fun Modifier.cornerRadiusSm(): Modifier = this
    .clip(androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.sm))

fun Modifier.cornerRadiusMd(): Modifier = this
    .clip(androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.md))

fun Modifier.cornerRadiusLg(): Modifier = this
    .clip(androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.lg))

fun Modifier.cornerRadiusXl(): Modifier = this
    .clip(androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.xl))

fun Modifier.cornerRadiusFull(): Modifier = this
    .clip(androidx.compose.foundation.shape.CircleShape)

// ============================================
// BORDER SHORTCUTS
// ============================================

/**
 * Extension для швидкого border
 */
fun Modifier.borderSubtle(shape: Shape = RectangleShape): Modifier = this
    .border(1.dp, BorderColors.subtle, shape)

fun Modifier.borderDefault(shape: Shape = RectangleShape): Modifier = this
    .border(1.dp, BorderColors.default, shape)

fun Modifier.borderAccent(shape: Shape = RectangleShape): Modifier = this
    .border(2.dp, BorderColors.accent, shape)

/**
 * ПРИКЛАД КОМПЛЕКСНОГО ВИКОРИСТАННЯ:
 * 
 * ```kotlin
 * // Glass card з тінню
 * Box(
 *     modifier = Modifier
 *         .size(200.dp, 100.dp)
 *         .glassEffect(
 *             strength = GlassStrength.MEDIUM,
 *             shape = RoundedCornerShape(CornerRadius.lg)
 *         )
 *         .shadowPreset(ShadowPreset.CARD, RoundedCornerShape(CornerRadius.lg))
 *         .cardPadding()
 * )
 * 
 * // CTA кнопка з градієнтом
 * Button(
 *     onClick = { },
 *     modifier = Modifier
 *         .gradientBackground(
 *             gradient = Gradients.secondary,
 *             shape = RoundedCornerShape(CornerRadius.md)
 *         )
 *         .shadowPreset(ShadowPreset.BUTTON_CTA, RoundedCornerShape(CornerRadius.md))
 * ) {
 *     Text("Почати урок")
 * }
 * 
 * // Картка курсу
 * Card(
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .cornerRadiusLg()
 *         .shadowPreset(ShadowPreset.CARD, RoundedCornerShape(CornerRadius.lg))
 * ) {
 *     Column(modifier = Modifier.cardPadding()) {
 *         // Content
 *     }
 * }
 * ```
 */

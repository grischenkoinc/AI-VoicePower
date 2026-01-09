package com.aivoicepower.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ===== GRADIENT BACKGROUNDS =====

fun Modifier.gradientBackground(
    gradient: Brush,
    shape: Shape = RoundedCornerShape(0.dp)
) = this
    .clip(shape)
    .background(gradient)

// Shortcuts для конкретних градієнтів
fun Modifier.cardHeaderBackground(shape: Shape = RoundedCornerShape(0.dp)) =
    gradientBackground(Gradients.cardHeader, shape)
fun Modifier.cardBodyBackground(shape: Shape = RoundedCornerShape(0.dp)) =
    gradientBackground(Gradients.cardBody, shape)
fun Modifier.primaryButtonBackground(shape: Shape = RoundedCornerShape(16.dp)) =
    gradientBackground(Gradients.primaryButton, shape)
fun Modifier.ctaButtonBackground(shape: Shape = RoundedCornerShape(16.dp)) =
    gradientBackground(Gradients.ctaButton, shape)

// ===== GLASS EFFECT =====

enum class GlassStrength {
    LIGHT, MEDIUM, STRONG
}

fun Modifier.glassEffect(
    strength: GlassStrength = GlassStrength.MEDIUM,
    shape: Shape = RoundedCornerShape(16.dp)
) = this
    .clip(shape)
    .background(
        when (strength) {
            GlassStrength.LIGHT -> GlassColors.light
            GlassStrength.MEDIUM -> GlassColors.medium
            GlassStrength.STRONG -> GlassColors.strong
        }
    )
    .border(
        width = 1.dp,
        color = GlassColors.border,
        shape = shape
    )

// ===== CORNER RADIUS SHORTCUTS =====

fun Modifier.cornerRadiusXs() = clip(RoundedCornerShape(4.dp))
fun Modifier.cornerRadiusSm() = clip(RoundedCornerShape(8.dp))
fun Modifier.cornerRadiusMd() = clip(RoundedCornerShape(12.dp))
fun Modifier.cornerRadiusLg() = clip(RoundedCornerShape(16.dp))
fun Modifier.cornerRadiusXl() = clip(RoundedCornerShape(24.dp))
fun Modifier.cornerRadiusXxl() = clip(RoundedCornerShape(32.dp))
fun Modifier.cornerRadiusFull() = clip(RoundedCornerShape(100.dp))

// ===== SHADOW PRESETS =====

enum class ShadowPreset {
    CARD,
    CARD_ELEVATED,
    BUTTON_PRIMARY,
    BUTTON_CTA,
    RECORD_BUTTON,
    RECORD_BUTTON_ACTIVE,
    LEVEL_BADGE,
    SECONDARY_BADGE,
    CIRCULAR_PROGRESS,
    SUCCESS_RING,
    PROGRESS_TRACK
}

fun Modifier.shadowPreset(
    preset: ShadowPreset,
    shape: Shape = RoundedCornerShape(16.dp)
) = this.shadow(
    elevation = when (preset) {
        ShadowPreset.CARD -> Elevation.level2
        ShadowPreset.CARD_ELEVATED -> Elevation.level3
        ShadowPreset.BUTTON_PRIMARY -> Elevation.level2
        ShadowPreset.BUTTON_CTA -> Elevation.level3
        ShadowPreset.RECORD_BUTTON -> Elevation.level3
        ShadowPreset.RECORD_BUTTON_ACTIVE -> Elevation.level4
        ShadowPreset.LEVEL_BADGE -> Elevation.level1
        ShadowPreset.SECONDARY_BADGE -> Elevation.level1
        ShadowPreset.CIRCULAR_PROGRESS -> Elevation.level2
        ShadowPreset.SUCCESS_RING -> Elevation.level2
        ShadowPreset.PROGRESS_TRACK -> Elevation.level1
    },
    shape = shape,
    clip = false
)

// ===== SPACING SHORTCUTS =====

fun Modifier.paddingXs() = this.then(Modifier.padding(Spacing.xs))
fun Modifier.paddingSm() = this.then(Modifier.padding(Spacing.sm))
fun Modifier.paddingMd() = this.then(Modifier.padding(Spacing.md))
fun Modifier.paddingLg() = this.then(Modifier.padding(Spacing.lg))
fun Modifier.paddingXl() = this.then(Modifier.padding(Spacing.xl))

fun Modifier.screenPadding(): Modifier = this.padding(horizontal = Spacing.screenHorizontal)
fun Modifier.cardPadding(): Modifier = this.padding(Spacing.cardPadding)
fun Modifier.sectionSpacing(): Modifier = this.padding(vertical = Spacing.sectionSpacing)

// ===== BORDER SHORTCUTS =====

fun Modifier.borderLight(
    width: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(12.dp)
) = border(width, BorderColors.light, shape)

fun Modifier.borderDefault(
    width: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(12.dp)
) = border(width, BorderColors.default, shape)

fun Modifier.borderPrimary(
    width: Dp = 2.dp,
    shape: Shape = RoundedCornerShape(12.dp)
) = border(width, BorderColors.primary, shape)

// ===== COMBINED MODIFIERS (для швидкості) =====

// Картка з темною шапкою
fun Modifier.cardHeaderStyle() = this
    .cardHeaderBackground()
    .paddingMd()

// Світле тіло картки
fun Modifier.cardBodyStyle() = this
    .cardBodyBackground()
    .paddingMd()

// Primary кнопка (повний стиль)
fun Modifier.primaryButtonStyle() = this
    .primaryButtonBackground()
    .shadowPreset(ShadowPreset.BUTTON_PRIMARY)
    .paddingMd()

// CTA кнопка (повний стиль)
fun Modifier.ctaButtonStyle() = this
    .ctaButtonBackground()
    .shadowPreset(ShadowPreset.BUTTON_CTA)
    .paddingMd()

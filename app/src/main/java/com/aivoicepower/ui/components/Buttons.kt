package com.aivoicepower.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * Primary Button - основна дія
 * Використання: загальні дії, "Далі", "Зберегти"
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.98f
            else -> 1f
        },
        animationSpec = tween(
            durationMillis = AnimationDuration.short,
            easing = AnimationEasing.standard
        ),
        label = "primaryButtonScale"
    )

    Box(
        modifier = modifier
            .height(48.dp)
            .scale(scale)
            .primaryButtonBackground(RoundedCornerShape(CornerRadius.md))
            .shadowPreset(ShadowPreset.BUTTON_PRIMARY, RoundedCornerShape(CornerRadius.md))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                onClick = onClick
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = TextColors.primary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                icon?.let {
                    it()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextColors.primary
                )
            }
        }
    }
}

/**
 * Secondary Button - другорядна дія (Ghost style)
 * Використання: "Назад", "Скасувати", "Пропустити"
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.98f
            else -> 1f
        },
        animationSpec = tween(
            durationMillis = AnimationDuration.short,
            easing = AnimationEasing.standard
        ),
        label = "secondaryButtonScale"
    )

    Box(
        modifier = modifier
            .height(48.dp)
            .scale(scale)
            .background(
                color = GlassColors.medium,
                shape = RoundedCornerShape(CornerRadius.md)
            )
            .border(
                width = 1.dp,
                color = BorderColors.default,
                shape = RoundedCornerShape(CornerRadius.md)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                onClick = onClick
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = TextColors.primary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                icon?.let {
                    it()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextColors.primary
                )
            }
        }
    }
}

/**
 * CTA Button - найголовніша дія з градієнтом
 * Використання: "Почати урок", "Записати", "Купити Premium"
 */
@Composable
fun CTAButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.98f
            else -> 1f
        },
        animationSpec = tween(
            durationMillis = AnimationDuration.short,
            easing = AnimationEasing.standard
        ),
        label = "ctaButtonScale"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .scale(scale)
            .ctaButtonBackground(RoundedCornerShape(CornerRadius.lg))
            .shadowPreset(ShadowPreset.BUTTON_CTA, RoundedCornerShape(CornerRadius.lg))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                onClick = onClick
            )
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = TextColors.primary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                icon?.let {
                    it()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextColors.primary
                )
            }
        }
    }
}

/**
 * Text Button - мінімалістична кнопка без фону
 * Використання: "Детальніше", "Показати більше"
 */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val textColor by animateColorAsState(
        targetValue = when {
            !enabled -> TextColors.muted
            isPressed -> PrimaryColors.dark
            else -> PrimaryColors.default
        },
        animationSpec = tween(
            durationMillis = AnimationDuration.short,
            easing = AnimationEasing.standard
        ),
        label = "textButtonColor"
    )

    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        interactionSource = interactionSource,
        colors = ButtonDefaults.textButtonColors(
            contentColor = PrimaryColors.default,
            disabledContentColor = TextColors.muted
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = textColor
        )
    }
}

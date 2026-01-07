package com.aivoicepower.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .height(48.dp)
            .shadowPreset(
                ShadowPreset.BUTTON_PRIMARY,
                RoundedCornerShape(CornerRadius.md)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColors.default,
            contentColor = TextColors.onPrimary,
            disabledContainerColor = PrimaryColors.default.copy(alpha = 0.38f)
        ),
        shape = RoundedCornerShape(CornerRadius.md)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = TextColors.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

/**
 * Secondary Button - другорядна дія
 * Використання: "Назад", "Скасувати", "Пропустити"
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextColors.primary,
            disabledContentColor = TextColors.muted
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (enabled) BorderColors.default else BorderColors.subtle
        ),
        shape = RoundedCornerShape(CornerRadius.md)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
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
    icon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .gradientBackground(
                Gradients.secondary,
                RoundedCornerShape(CornerRadius.md)
            )
            .shadowPreset(
                ShadowPreset.BUTTON_CTA,
                RoundedCornerShape(CornerRadius.md)
            )
            .then(
                if (enabled && !loading) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = TextColors.onSecondary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = TextColors.onSecondary
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextColors.onSecondary
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
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = PrimaryColors.default,
            disabledContentColor = TextColors.muted
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

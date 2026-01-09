package com.aivoicepower.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

@Composable
fun TestDesignSystemScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColors.primary)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionSpacing)
    ) {
        // Заголовок
        Text(
            text = "Design System Test",
            style = MaterialTheme.typography.headlineLarge,
            color = TextColors.primary
        )

        Text(
            text = "Перевірка кольорів, типографіки та компонентів",
            style = MaterialTheme.typography.bodyMedium,
            color = TextColors.secondary
        )

        // Картка з інформацією
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .cornerRadiusLg(),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundColors.surface
            )
        ) {
            Column(
                modifier = Modifier.cardPadding(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "Інформаційна картка",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextColors.primary
                )

                Text(
                    text = "Це приклад картки з Design System. Використовує BackgroundColors.surface, правильні відступи та corner radius.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextColors.secondary
                )

                // Кольорові індикатори
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    ColorBox("Primary", PrimaryColors.default)
                    ColorBox("Secondary", SecondaryColors.default)
                    ColorBox("Accent", AccentColors.default)
                }
            }
        }

        // Primary Button
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColors.default
            ),
            shape = RoundedCornerShape(CornerRadius.md)
        ) {
            Text(
                "Primary Button",
                style = MaterialTheme.typography.labelLarge
            )
        }

        // CTA Button з градієнтом
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .gradientBackground(
                    Gradients.ctaButton,
                    RoundedCornerShape(CornerRadius.md)
                ),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                "CTA Button з градієнтом",
                color = TextColors.onSecondary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Glass effect card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassEffect(
                    GlassStrength.MEDIUM,
                    RoundedCornerShape(CornerRadius.xl)
                )
                .padding(Spacing.lg)
        ) {
            Text(
                text = "Glass Effect Card\nНапівпрозорий ефект скла",
                style = MaterialTheme.typography.bodyMedium,
                color = TextColors.primary
            )
        }

        // Статус індикатори
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatusChip("Success", SemanticColors.success)
            StatusChip("Warning", SemanticColors.warning)
            StatusChip("Error", SemanticColors.error)
        }
    }
}

@Composable
private fun ColorBox(label: String, color: androidx.compose.ui.graphics.Color) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .background(color, RoundedCornerShape(CornerRadius.sm))
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextColors.onPrimary
        )
    }
}

@Composable
private fun StatusChip(label: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(CornerRadius.full),
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = Spacing.md),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextColors.onPrimary
            )
        }
    }
}

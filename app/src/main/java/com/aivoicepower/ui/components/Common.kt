package com.aivoicepower.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * Status Chip - кольоровий chip для статусів
 */
@Composable
fun StatusChip(
    text: String,
    status: ChipStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        ChipStatus.SUCCESS -> SemanticColors.success to TextColors.onPrimary
        ChipStatus.WARNING -> SemanticColors.warning to TextColors.onPrimary
        ChipStatus.ERROR -> SemanticColors.error to TextColors.onPrimary
        ChipStatus.INFO -> SemanticColors.info to TextColors.onPrimary
        ChipStatus.NEUTRAL -> BackgroundColors.surface to TextColors.primary
    }

    Surface(
        modifier = modifier.height(32.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(CornerRadius.full)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = Spacing.md),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = textColor
            )
        }
    }
}

enum class ChipStatus {
    SUCCESS, WARNING, ERROR, INFO, NEUTRAL
}

/**
 * Info Badge - маленький badge з числом
 */
@Composable
fun InfoBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0) {
        Surface(
            modifier = modifier.size(20.dp),
            color = SemanticColors.error,
            shape = CircleShape
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = if (count > 99) "99+" else "$count",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextColors.onPrimary
                )
            }
        }
    }
}

/**
 * Section Divider - розділювач з текстом
 */
@Composable
fun SectionDivider(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = BorderColors.default
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = TextColors.secondary
        )
        Divider(
            modifier = Modifier.weight(1f),
            color = BorderColors.default
        )
    }
}

/**
 * Empty State - порожній стан з іконкою та текстом
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextColors.muted
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextColors.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextColors.secondary,
            textAlign = TextAlign.Center
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            PrimaryButton(
                text = actionText,
                onClick = onActionClick
            )
        }
    }
}

/**
 * Loading Indicator - індикатор завантаження з текстом
 */
@Composable
fun LoadingIndicator(
    text: String = "Завантаження...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        CircularProgressIndicator(
            color = PrimaryColors.default,
            strokeWidth = 3.dp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextColors.secondary
        )
    }
}

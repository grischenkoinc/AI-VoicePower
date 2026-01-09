package com.aivoicepower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*

/**
 * Chip Status enum
 */
enum class ChipStatus {
    SUCCESS, WARNING, ERROR, INFO, NEUTRAL
}

/**
 * Status Chip - кольоровий chip для статусів
 */
@Composable
fun StatusChip(
    text: String,
    status: ChipStatus,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val (backgroundColor, textColor) = when (status) {
        ChipStatus.SUCCESS -> SuccessColors.light.copy(alpha = 0.15f) to SuccessColors.default
        ChipStatus.WARNING -> WarningColors.light.copy(alpha = 0.15f) to WarningColors.default
        ChipStatus.ERROR -> ErrorColors.light.copy(alpha = 0.15f) to ErrorColors.default
        ChipStatus.INFO -> PrimaryColors.light.copy(alpha = 0.15f) to PrimaryColors.default
        ChipStatus.NEUTRAL -> GlassColors.medium to TextColors.secondary
    }

    Row(
        modifier = modifier
            .height(24.dp)
            .background(backgroundColor, RoundedCornerShape(CornerRadius.full))
            .padding(horizontal = Spacing.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

/**
 * Info Badge - маленький круглий badge з числом
 */
@Composable
fun InfoBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0) {
        Box(
            modifier = modifier
                .size(20.dp)
                .shadowPreset(ShadowPreset.SECONDARY_BADGE, CircleShape)
                .background(SecondaryColors.default, CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color.White
            )
        }
    }
}

/**
 * Section Divider - розділювач секцій з опціональним текстом
 */
@Composable
fun SectionDivider(
    modifier: Modifier = Modifier,
    label: String? = null
) {
    if (label != null) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = BorderColors.default
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextColors.tertiary,
                modifier = Modifier.padding(horizontal = Spacing.sm)
            )
            Divider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = BorderColors.default
            )
        }
    } else {
        Divider(
            modifier = modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = BorderColors.default
        )
    }
}

/**
 * Empty State - порожній стан списку з іконкою та текстом
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
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

        if (actionLabel != null && onActionClick != null) {
            AppTextButton(
                text = actionLabel,
                onClick = onActionClick
            )
        }
    }
}

/**
 * Loading Indicator - індикатор завантаження з опціональним текстом
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    text: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = PrimaryColors.default,
            strokeWidth = 3.dp
        )

        text?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = TextColors.secondary
            )
        }
    }
}

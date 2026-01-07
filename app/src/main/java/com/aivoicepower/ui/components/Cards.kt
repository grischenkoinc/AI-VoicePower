package com.aivoicepower.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * Course Card - картка курсу
 */
@Composable
fun CourseCard(
    title: String,
    description: String,
    lessonsCount: Int,
    progress: Float, // 0.0 - 1.0
    isLocked: Boolean = false,
    isPremium: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .cornerRadiusLg()
            .shadowPreset(
                ShadowPreset.CARD,
                RoundedCornerShape(CornerRadius.lg)
            )
            .then(if (!isLocked) Modifier.clickable(onClick = onClick) else Modifier),
        color = BackgroundColors.surface,
        shape = RoundedCornerShape(CornerRadius.lg)
    ) {
        Column(
            modifier = Modifier
                .cardPadding()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon + Title
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = PrimaryColors.default
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextColors.primary
                    )
                }

                // Premium badge or Lock
                if (isPremium) {
                    PremiumBadge()
                }
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Заблоковано",
                        tint = TextColors.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextColors.secondary,
                maxLines = 2
            )

            // Progress bar
            if (progress > 0f && !isLocked) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = AccentColors.default,
                        trackColor = BackgroundColors.primary.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% завершено",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextColors.secondary
                    )
                }
            }

            // Lessons count
            Text(
                text = "$lessonsCount уроків",
                style = MaterialTheme.typography.labelMedium,
                color = TextColors.muted
            )
        }
    }
}

/**
 * Lesson Card - картка уроку
 */
@Composable
fun LessonCard(
    dayNumber: Int,
    title: String,
    estimatedMinutes: Int,
    isCompleted: Boolean = false,
    isLocked: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .cornerRadiusMd()
            .shadowPreset(
                if (isCompleted) ShadowPreset.ELEVATED else ShadowPreset.CARD,
                RoundedCornerShape(CornerRadius.md)
            )
            .then(if (!isLocked) Modifier.clickable(onClick = onClick) else Modifier),
        color = if (isCompleted)
            BackgroundColors.surfaceElevated
        else
            BackgroundColors.surface,
        shape = RoundedCornerShape(CornerRadius.md)
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day number circle
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isCompleted)
                    SemanticColors.success
                else if (isLocked)
                    BackgroundColors.primary.copy(alpha = 0.5f)
                else
                    PrimaryColors.default
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Завершено",
                            tint = TextColors.onPrimary
                        )
                    } else if (isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Заблоковано",
                            tint = TextColors.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "$dayNumber",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextColors.onPrimary
                        )
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextColors.primary
                )
                Text(
                    text = "~$estimatedMinutes хв",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextColors.secondary
                )
            }

            // Arrow
            if (!isLocked) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextColors.secondary
                )
            }
        }
    }
}

/**
 * Premium Badge
 */
@Composable
fun PremiumBadge() {
    Surface(
        shape = RoundedCornerShape(CornerRadius.full),
        color = SecondaryColors.default,
        modifier = Modifier.height(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PRO",
                style = MaterialTheme.typography.labelSmall,
                color = TextColors.onPrimary
            )
        }
    }
}

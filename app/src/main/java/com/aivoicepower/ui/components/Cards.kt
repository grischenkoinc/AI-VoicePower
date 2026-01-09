package com.aivoicepower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * Course Card - картка курсу з темною фіолетовою шапкою та світлим body
 */
@Composable
fun CourseCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    progress: Float?, // null якщо не почато
    totalLessons: Int,
    completedLessons: Int,
    isLocked: Boolean = false,
    isCompleted: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadowPreset(ShadowPreset.CARD, RoundedCornerShape(CornerRadius.lg))
            .clickable(enabled = !isLocked, onClick = onClick),
        shape = RoundedCornerShape(CornerRadius.lg),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box {
            Column {
                // Header (темна фіолетова)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .cardHeaderBackground()
                        .padding(Spacing.lg)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        // Icon в кольоровому колі
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(iconColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = Color.White
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextColors.primary
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextColors.secondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Body (світлий)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .cardBodyBackground()
                        .then(
                            if (isCompleted) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = SuccessColors.default,
                                    shape = RoundedCornerShape(
                                        bottomStart = CornerRadius.lg,
                                        bottomEnd = CornerRadius.lg
                                    )
                                )
                            } else Modifier
                        )
                        .padding(Spacing.lg)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        if (progress != null && progress > 0f) {
                            SkillProgressBar(
                                skillName = "",
                                progress = progress,
                                showLabel = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$completedLessons / $totalLessons уроків",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextColors.tertiary
                            )

                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    modifier = Modifier.size(20.dp),
                                    tint = SuccessColors.default
                                )
                            }
                        }
                    }
                }
            }

            // Locked overlay
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Lesson Card - картка уроку з темною шапкою та світлим body
 */
@Composable
fun LessonCard(
    dayNumber: Int,
    title: String,
    durationMinutes: Int,
    exercisesCount: Int,
    isCompleted: Boolean = false,
    isLocked: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.6f else 1f)
            .shadowPreset(
                if (isCompleted) ShadowPreset.CARD_ELEVATED else ShadowPreset.CARD,
                RoundedCornerShape(CornerRadius.lg)
            )
            .clickable(enabled = !isLocked, onClick = onClick),
        shape = RoundedCornerShape(CornerRadius.lg),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .then(
                        if (isCompleted) {
                            Modifier.gradientBackground(
                                Gradients.success,
                                RoundedCornerShape(
                                    topStart = CornerRadius.lg,
                                    topEnd = CornerRadius.lg
                                )
                            )
                        } else {
                            Modifier.cardHeaderBackground()
                        }
                    )
                    .padding(Spacing.md)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Day circle або icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (isCompleted) Color.White else PrimaryColors.default,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isCompleted -> {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    modifier = Modifier.size(20.dp),
                                    tint = SuccessColors.default
                                )
                            }
                            isLocked -> {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                            else -> {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextColors.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .cardBodyBackground()
                    .padding(Spacing.md)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Duration",
                            modifier = Modifier.size(16.dp),
                            tint = TextColors.tertiary
                        )
                        Text(
                            text = "$durationMinutes хв",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextColors.tertiary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Exercises",
                            modifier = Modifier.size(16.dp),
                            tint = TextColors.tertiary
                        )
                        Text(
                            text = "$exercisesCount вправ",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextColors.tertiary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Premium Badge - золотий бейдж Premium
 */
@Composable
fun PremiumBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(24.dp)
            .gradientBackground(Gradients.premium, RoundedCornerShape(CornerRadius.full))
            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(CornerRadius.full))
            .shadowPreset(ShadowPreset.SECONDARY_BADGE, RoundedCornerShape(CornerRadius.full))
            .padding(horizontal = Spacing.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Premium",
            modifier = Modifier.size(14.dp),
            tint = Color.White
        )
        Text(
            text = "Premium",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.aivoicepower.ui.screens.courses.LessonWithProgress
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.modifiers.*

@Composable
fun LessonListItem(
    lessonWithProgress: LessonWithProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = lessonWithProgress.isCompleted
    val isLocked = lessonWithProgress.isLocked

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (isLocked) 0.55f else 1f)
                .shadow(
                    elevation = if (isLocked) 6.dp else 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = if (isCompleted) {
                        Color(0xFF22C55E).copy(alpha = 0.2f)
                    } else {
                        Color.Black.copy(alpha = 0.1f)
                    }
                )
                .background(
                    if (isCompleted) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFF0FDF4),
                                Color(0xFFDCFCE7)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFFAFAFA)
                            )
                        )
                    },
                    RoundedCornerShape(20.dp)
                )
                .scaleOnPress(pressedScale = 0.97f)
                .clickable(onClick = onClick)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = if (isCompleted) 8.dp else 4.dp,
                        shape = CircleShape,
                        spotColor = when {
                            isCompleted -> Color(0xFF22C55E).copy(alpha = 0.4f)
                            isLocked -> Color.Black.copy(alpha = 0.15f)
                            else -> Color(0xFF667EEA).copy(alpha = 0.3f)
                        }
                    )
                    .background(
                        when {
                            isCompleted -> Brush.linearGradient(
                                colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                            )
                            isLocked -> Brush.linearGradient(
                                colors = listOf(Color(0xFFE5E7EB), Color(0xFFD1D5DB))
                            )
                            else -> Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            )
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        isCompleted -> "\u2713"
                        isLocked -> "\uD83D\uDD12"
                        else -> "${lessonWithProgress.lesson.dayNumber}"
                    },
                    color = Color.White,
                    fontSize = if (isCompleted || isLocked) 24.sp else 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Lesson info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = lessonWithProgress.lesson.title,
                    style = AppTypography.titleMedium,
                    color = if (isCompleted) {
                        Color(0xFF15803D)
                    } else {
                        TextColors.onLightPrimary
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "День ${lessonWithProgress.lesson.dayNumber}",
                        style = AppTypography.bodySmall,
                        color = if (isCompleted) {
                            Color(0xFF22C55E)
                        } else if (isLocked) {
                            TextColors.onLightMuted
                        } else {
                            TextColors.onLightSecondary
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(text = "\u2022", color = TextColors.onLightMuted, fontSize = 13.sp)

                    Text(
                        text = "${lessonWithProgress.lesson.exercises.size} вправи",
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(text = "\u2022", color = TextColors.onLightMuted, fontSize = 13.sp)

                    Text(
                        text = "${lessonWithProgress.lesson.estimatedMinutes} хв",
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Completion badge
                if (isCompleted) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF22C55E), CircleShape)
                        )
                        Text(
                            text = "Завершено",
                            style = AppTypography.labelSmall,
                            color = Color(0xFF22C55E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Arrow or lock icon
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isLocked) "" else "\u2192",
                    fontSize = 20.sp,
                    color = if (isCompleted) {
                        Color(0xFF22C55E)
                    } else {
                        Color(0xFF667EEA)
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Pro badge — top-right corner
        if (isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = (-7).dp)
                    .zIndex(1f)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(7.dp),
                        spotColor = Color(0xFF7C3AED).copy(alpha = 0.5f),
                        ambientColor = Color(0xFF4C1D95).copy(alpha = 0.2f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF9333EA),
                                Color(0xFF7C3AED),
                                Color(0xFF6D28D9)
                            )
                        ),
                        RoundedCornerShape(7.dp)
                    )
                    .border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(7.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "PRO",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

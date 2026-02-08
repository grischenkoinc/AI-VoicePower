package com.aivoicepower.ui.screens.courses.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.ui.screens.progress.components.AchievementBadge
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*

@Composable
fun CompletedPhaseContent(
    lesson: Lesson,
    nextLesson: Lesson? = null,
    isLastLessonInCourse: Boolean = false,
    courseName: String = "",
    courseBadge: Achievement? = null,
    onFinish: () -> Unit,
    onNextLesson: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Celebration animation - only on appear
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    GradientBackground(
        content = {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLastLessonInCourse) {
                    // Course Completion - Gold trophy
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(scale)
                            .shadow(
                                elevation = 32.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFFFFD700).copy(alpha = 0.6f)
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFF59E0B))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\uD83C\uDFC6",
                            fontSize = 72.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "Курс завершено!",
                        style = AppTypography.displayLarge,
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-1.5).sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = courseName,
                        style = AppTypography.bodyLarge,
                        color = Color(0xFFFFD700),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Stats Card with badge
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(32.dp),
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .background(
                                Color.White.copy(alpha = 0.95f),
                                RoundedCornerShape(32.dp)
                            )
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Неймовірний результат!",
                            style = AppTypography.titleLarge,
                            color = TextColors.onLightPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Ти пройшов увесь курс \"$courseName\"! Твої навички значно покращились.",
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        // Course badge
                        if (courseBadge != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AchievementBadge(
                                achievement = courseBadge,
                                isLarge = true,
                                modifier = Modifier.fillMaxWidth(0.6f)
                            )
                        }
                    }
                } else {
                    // Regular Lesson Completion - Green checkmark
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(scale)
                            .shadow(
                                elevation = 24.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFF22C55E).copy(alpha = 0.5f)
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 72.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Урок пройдено!",
                        style = AppTypography.displayLarge,
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-1.5).sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "День ${lesson.dayNumber}: ${lesson.title}",
                        style = AppTypography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Stats Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(32.dp),
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .background(
                                Color.White.copy(alpha = 0.95f),
                                RoundedCornerShape(32.dp)
                            )
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Чудова робота!",
                            style = AppTypography.titleLarge,
                            color = TextColors.onLightPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Прогрес збережено.",
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isLastLessonInCourse && nextLesson != null && onNextLesson != null) {
                        NextLessonButton(
                            lessonTitle = nextLesson.title,
                            onClick = onNextLesson,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    NavButton(
                        text = "Повернутися до курсу",
                        icon = "←",
                        isPrimary = false,
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

@Composable
private fun NextLessonButton(
    lessonTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        modifier = modifier
            .shadow(
                elevation = if (isPressed) 12.dp else 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "ДО НАСТУПНОГО УРОКУ:",
            style = AppTypography.labelMedium,
            color = Color(0xFF667EEA),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )

        Text(
            text = lessonTitle.uppercase(),
            style = AppTypography.labelLarge,
            color = Color(0xFF667EEA),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )
    }
}

package com.aivoicepower.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.courses.components.*
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*

@Composable
fun CourseDetailScreen(
    courseId: String,
    viewModel: CourseDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (courseId: String, lessonId: String) -> Unit,
    onNavigateToPremium: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = Color.White,
                        strokeWidth = 5.dp
                    )
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(32.dp),
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .background(Color.White, RoundedCornerShape(32.dp))
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(text = "⚠️", fontSize = 64.sp)

                        Text(
                            text = "Помилка",
                            style = AppTypography.titleLarge,
                            color = TextColors.onLightPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = state.error!!,
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightSecondary,
                            fontSize = 16.sp
                        )

                        PrimaryButton(
                            text = "Повторити",
                            onClick = { viewModel.onEvent(CourseDetailEvent.Refresh) }
                        )
                    }
                }
            }

            state.course != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 60.dp,
                        bottom = 130.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Back button
                    item {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = CircleShape,
                                    spotColor = Color.Black.copy(alpha = 0.2f)
                                )
                                .background(Color.White, CircleShape)
                                .clickable { onNavigateBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "←", fontSize = 24.sp, color = TextColors.onLightPrimary)
                        }
                    }

                    // Course header
                    item {
                        CourseHeaderCard(
                            course = state.course!!,
                            completedLessons = state.completedLessons,
                            totalLessons = state.totalLessons,
                            progressPercent = state.progressPercent
                        )
                    }

                    // Lessons grouped by week
                    val lessonsByWeek = state.lessonsWithProgress.groupBy { it.weekNumber }

                    lessonsByWeek.forEach { (weekNumber, lessons) ->
                        item {
                            Text(
                                text = "Тиждень $weekNumber",
                                style = AppTypography.titleLarge,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                            )
                        }

                        items(lessons) { lessonWithProgress ->
                            LessonListItem(
                                lessonWithProgress = lessonWithProgress,
                                onClick = {
                                    if (!lessonWithProgress.isLocked) {
                                        viewModel.onEvent(CourseDetailEvent.LessonClicked(lessonWithProgress.lesson.id))
                                        onNavigateToLesson(courseId, lessonWithProgress.lesson.id)
                                    }
                                }
                            )
                        }

                        // Premium upgrade prompt
                        if (lessons.any { it.isLocked } && !state.isPremium) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 16.dp,
                                            shape = RoundedCornerShape(24.dp),
                                            spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
                                        )
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFFFFFBEB),
                                                    Color(0xFFFEF3C7)
                                                )
                                            ),
                                            RoundedCornerShape(24.dp)
                                        )
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            Color(0xFFFBBF24),
                                                            Color(0xFFF59E0B)
                                                        )
                                                    ),
                                                    RoundedCornerShape(16.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "🔒", fontSize = 28.sp)
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Розблокуй всі уроки",
                                                style = AppTypography.titleMedium,
                                                color = Color(0xFFD97706),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = "Отримай доступ до ${lessons.size} уроків Premium",
                                                style = AppTypography.bodyMedium,
                                                color = Color(0xFF92400E),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    PrimaryButton(
                                        text = "Оновити до Premium",
                                        onClick = {
                                            viewModel.onEvent(CourseDetailEvent.UpgradeToPremiumClicked)
                                            onNavigateToPremium()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

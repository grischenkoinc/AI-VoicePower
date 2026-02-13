package com.aivoicepower.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                    verticalArrangement = Arrangement.spacedBy(17.dp)
                ) {
                    // Back button
                    item {
                        Row(
                            modifier = Modifier
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color.Black.copy(alpha = 0.2f)
                                )
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .clickable { onNavigateBack() }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "←",
                                fontSize = 24.sp,
                                color = Color(0xFF667EEA),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Назад",
                                style = AppTypography.bodyMedium,
                                color = TextColors.onLightPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
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
                        val weekHasPremiumLocked = lessons.any { it.lockReason == LockReason.PremiumRequired }
                        val isFirstPremiumLockedWeek = weekHasPremiumLocked &&
                            lessonsByWeek.entries.firstOrNull { (_, wLessons) ->
                                wLessons.any { it.lockReason == LockReason.PremiumRequired }
                            }?.key == weekNumber

                        // Pro divider banner before the first premium-locked week
                        if (isFirstPremiumLockedWeek && !state.isUserPremium) {
                            item {
                                ProWeekDivider(
                                    lockedLessonsCount = state.lessonsWithProgress.count {
                                        it.lockReason == LockReason.PremiumRequired
                                    },
                                    onPremiumClick = onNavigateToPremium
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Тиждень $weekNumber",
                                    style = AppTypography.titleLarge,
                                    color = if (weekHasPremiumLocked && !state.isUserPremium)
                                        Color.White.copy(alpha = 0.6f)
                                    else Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                if (weekHasPremiumLocked && !state.isUserPremium) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFF9333EA),
                                                        Color(0xFF7C3AED),
                                                        Color(0xFF6D28D9)
                                                    )
                                                ),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "PRO",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                        }

                        items(lessons) { lessonWithProgress ->
                            LessonListItem(
                                lessonWithProgress = lessonWithProgress,
                                onClick = {
                                    when (lessonWithProgress.lockReason) {
                                        LockReason.None -> {
                                            viewModel.onEvent(CourseDetailEvent.LessonClicked(lessonWithProgress.lesson.id))
                                            onNavigateToLesson(courseId, lessonWithProgress.lesson.id)
                                        }
                                        LockReason.PrerequisiteNotMet -> {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "Спочатку завершіть попередні уроки"
                                                )
                                            }
                                        }
                                        LockReason.PremiumRequired -> {
                                            onNavigateToPremium()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

@Composable
private fun ProWeekDivider(
    lockedLessonsCount: Int,
    onPremiumClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
    ) {
        // Gradient divider line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF9333EA),
                            Color(0xFF7C3AED),
                            Color(0xFF9333EA),
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pro card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color(0xFF7C3AED).copy(alpha = 0.4f)
                )
                .background(Color.White, RoundedCornerShape(20.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF9333EA), Color(0xFF667EEA))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onPremiumClick() }
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mic icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667EEA),
                                Color(0xFF764BA2),
                                Color(0xFF9333EA)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Розблокуй повний курс",
                color = Color(0xFF1F2937),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ще $lockedLessonsCount уроків з детальним AI аналізом",
                color = Color(0xFF6B7280),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // CTA button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Отримати PRO",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

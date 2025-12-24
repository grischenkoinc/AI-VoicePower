package com.aivoicepower.ui.screens.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.courses.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    viewModel: CourseDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (courseId: String, lessonId: String) -> Unit,
    onNavigateToPremium: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.course?.title ?: "Курс") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(state.error!!)
                        Button(onClick = { viewModel.onEvent(CourseDetailEvent.Refresh) }) {
                            Text("Повторити")
                        }
                    }
                }
            }

            state.course != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Course header
                    item {
                        CourseHeaderCard(
                            course = state.course!!,
                            completedLessons = state.completedLessons,
                            totalLessons = state.totalLessons,
                            progressPercent = state.progressPercent
                        )
                    }

                    // Stats card
                    item {
                        CourseStatsCard(course = state.course!!)
                    }

                    // Lessons grouped by week
                    val lessonsByWeek = state.lessonsWithProgress.groupBy { it.weekNumber }

                    lessonsByWeek.forEach { (weekNumber, lessons) ->
                        item {
                            Text(
                                text = "📋 Уроки тижня $weekNumber (${lessons.first().lesson.dayNumber}-${lessons.last().lesson.dayNumber})" +
                                        if (lessons.any { it.isLocked }) " 🔒" else "",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
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
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "🔒 Уроки ${lessons.first().lesson.dayNumber}+ доступні в Premium",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Button(
                                            onClick = {
                                                viewModel.onEvent(CourseDetailEvent.UpgradeToPremiumClicked)
                                                onNavigateToPremium()
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Оновити до Premium")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

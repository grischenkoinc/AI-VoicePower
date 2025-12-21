package com.aivoicepower.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.home.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCourse: (String) -> Unit,
    onNavigateToAiCoach: () -> Unit,
    onNavigateToLesson: (courseId: String, lessonId: String) -> Unit,
    onNavigateToWarmup: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToImprovisation: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToQuickWarmup: () -> Unit,
    onNavigateToRandomTopic: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI VoicePower") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(HomeEvent.AiCoachClicked)
                    onNavigateToAiCoach()
                }
            ) {
                Icon(Icons.Default.Assistant, contentDescription = "AI Тренер")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
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
                    Button(onClick = { viewModel.onEvent(HomeEvent.Refresh) }) {
                        Text("Повторити")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome Header
                item {
                    WelcomeHeader(
                        greeting = state.greeting,
                        userName = state.userName,
                        currentStreak = state.currentStreak
                    )
                }

                // Today's Plan
                item {
                    state.todayPlan?.let { plan ->
                        TodayPlanCard(
                            plan = plan,
                            onActivityClick = { activity ->
                                when (activity.navigationRoute) {
                                    com.aivoicepower.ui.navigation.NavRoutes.QuickWarmup.route ->
                                        onNavigateToQuickWarmup()
                                    com.aivoicepower.ui.navigation.NavRoutes.AiCoach.route ->
                                        onNavigateToAiCoach()
                                    com.aivoicepower.ui.navigation.NavRoutes.RandomTopic.route ->
                                        onNavigateToRandomTopic()
                                    else -> {
                                        // Parse lesson route
                                        val parts = activity.navigationRoute.split("/")
                                        if (parts.size >= 4 && parts[0] == "courses") {
                                            onNavigateToLesson(parts[1], parts[3])
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                // Quick Actions
                item {
                    QuickActionsGrid(
                        onWarmupClick = {
                            viewModel.onEvent(HomeEvent.WarmupClicked)
                            onNavigateToWarmup()
                        },
                        onCoursesClick = {
                            viewModel.onEvent(HomeEvent.CoursesClicked)
                            onNavigateToCourses()
                        },
                        onImprovisationClick = {
                            viewModel.onEvent(HomeEvent.ImprovisationClicked)
                            onNavigateToImprovisation()
                        },
                        onProgressClick = {
                            viewModel.onEvent(HomeEvent.ProgressClicked)
                            onNavigateToProgress()
                        }
                    )
                }

                // Week Progress
                item {
                    state.weekProgress?.let { weekProgress ->
                        WeekProgressChart(weekProgress = weekProgress)
                    }
                }
            }
        }
    }
}

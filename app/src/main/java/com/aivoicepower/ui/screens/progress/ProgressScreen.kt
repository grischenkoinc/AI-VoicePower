package com.aivoicepower.ui.screens.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.progress.components.AchievementBadge
import com.aivoicepower.ui.screens.progress.components.OverallLevelCard
import com.aivoicepower.ui.screens.progress.components.ProgressLineChart
import com.aivoicepower.ui.screens.progress.components.SkillBarChart
import com.aivoicepower.ui.screens.progress.components.SkillRadarChart
import com.aivoicepower.ui.screens.progress.components.StatsCard
import com.aivoicepower.ui.screens.progress.components.StreakCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
    onNavigateToCompare: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Прогрес") },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ProgressEvent.Refresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Оновити")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overall Level
                item {
                    OverallLevelCard(
                        level = state.overallLevel,
                        levelLabel = getLevelLabel(state.overallLevel)
                    )
                }

                // Streak
                item {
                    StreakCard(
                        currentStreak = state.currentStreak,
                        longestStreak = state.longestStreak
                    )
                }

                // Stats Overview
                item {
                    StatsCard(
                        totalExercises = state.totalExercises,
                        totalMinutes = state.totalMinutes,
                        totalRecordings = state.totalRecordings
                    )
                }

                // Skill Levels
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Навички",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Radar Chart
                            SkillRadarChart(
                                skillLevels = state.skillLevels,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Bar Chart
                            SkillBarChart(
                                skillLevels = state.skillLevels
                            )
                        }
                    }
                }

                // Weekly Progress Chart
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Активнiсть (7 днiв)",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ProgressLineChart(
                                weeklyProgress = state.weeklyProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                        }
                    }
                }

                // Recent Achievements
                if (state.recentAchievements.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Останнi досягнення",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    TextButton(onClick = onNavigateToAchievements) {
                                        Text("Всi")
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                state.recentAchievements.forEach { achievement ->
                                    AchievementBadge(
                                        achievement = achievement,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Вiдкрито: ${state.unlockedAchievements}/${state.totalAchievements}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Quick Actions
                item {
                    Text(
                        text = "Дiї",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    OutlinedCard(
                        onClick = onNavigateToCompare,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Compare, contentDescription = null)
                                Text("Порiвняти з початком")
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }

                item {
                    OutlinedCard(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.History, contentDescription = null)
                                Text("Iсторiя записiв")
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

private fun getLevelLabel(level: Int): String {
    return when {
        level < 20 -> "Початкiвець"
        level < 40 -> "Практикуючий"
        level < 60 -> "Досвiдчений"
        level < 80 -> "Майстер"
        else -> "Професiонал"
    }
}

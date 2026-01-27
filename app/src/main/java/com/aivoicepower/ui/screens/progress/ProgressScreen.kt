package com.aivoicepower.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.progress.components.*
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
    onNavigateToCompare: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            ProgressContent(
                state = state,
                onNavigateToCompare = onNavigateToCompare,
                onNavigateToAchievements = onNavigateToAchievements,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateBack = onNavigateBack,
                onRefresh = { viewModel.onEvent(ProgressEvent.Refresh) }
            )
        }
    }
}

@Composable
private fun ProgressContent(
    state: ProgressState,
    onNavigateToCompare: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        ProgressHeader(
            onNavigateBack = onNavigateBack,
            onRefresh = onRefresh
        )

        // Overall Level
        OverallLevelCard(
            level = state.overallLevel,
            levelLabel = getLevelLabel(state.overallLevel)
        )

        // Streak
        StreakCard(
            currentStreak = state.currentStreak,
            longestStreak = state.longestStreak
        )

        // Stats Overview
        StatsCard(
            totalExercises = state.totalExercises,
            totalMinutes = state.totalMinutes,
            totalRecordings = state.totalRecordings
        )

        // Skill Levels
        Text(
            text = "Навички",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.18f),
                    ambientColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Radar Chart
            SkillRadarChart(
                skillLevels = state.skillLevels,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // Bar Chart
            SkillBarChart(
                skillLevels = state.skillLevels
            )
        }

        // Weekly Progress Chart
        Text(
            text = "Активність (7 днів)",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.18f)
                )
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            ProgressLineChart(
                weeklyProgress = state.weeklyProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }

        // Recent Achievements
        if (state.recentAchievements.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Останні досягнення",
                    style = AppTypography.titleLarge,
                    color = TextColors.onDarkPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Всі →",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onDarkSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToAchievements() }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 0.18f)
                    )
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.recentAchievements.forEach { achievement ->
                    AchievementBadge(
                        achievement = achievement,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = "Відкрито: ${state.unlockedAchievements}/${state.totalAchievements}",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightMuted,
                    fontSize = 13.sp
                )
            }
        }

        // Quick Actions
        Text(
            text = "Дії",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )

        ActionCard(
            icon = Icons.Default.Compare,
            title = "Порівняти з початком",
            onClick = onNavigateToCompare
        )

        ActionCard(
            icon = Icons.Default.History,
            title = "Історія записів",
            onClick = onNavigateToHistory
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProgressHeader(
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Твої досягнення",
                style = AppTypography.labelMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Прогрес",
                style = AppTypography.displayLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Refresh button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF10B981), Color(0xFF14B8A6))
                        ),
                        CircleShape
                    )
                    .clickable { onRefresh() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "↻", fontSize = 20.sp, color = Color.White)
            }

            // Back button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        ),
                        CircleShape
                    )
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "←", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = title,
                style = AppTypography.bodyLarge,
                color = TextColors.onLightPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextColors.onLightMuted
        )
    }
}

private fun getLevelLabel(level: Int): String {
    return when {
        level < 20 -> "Початківець"
        level < 40 -> "Практикуючий"
        level < 60 -> "Досвідчений"
        level < 80 -> "Майстер"
        else -> "Професіонал"
    }
}

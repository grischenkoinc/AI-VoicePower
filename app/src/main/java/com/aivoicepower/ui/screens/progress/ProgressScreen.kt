package com.aivoicepower.ui.screens.progress

import android.view.HapticFeedbackConstants
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalConfiguration
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
import com.aivoicepower.ui.components.ProgressSkeletonContent
import com.aivoicepower.ui.screens.progress.components.*
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.modifiers.scaleOnPress
import com.aivoicepower.ui.theme.modifiers.staggeredEntry

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
    onNavigateToCompare: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSkillDetail: (com.aivoicepower.domain.model.user.SkillType) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        if (state.isLoading) {
            ProgressSkeletonContent()
        } else {
            ProgressContent(
                state = state,
                onNavigateToCompare = onNavigateToCompare,
                onNavigateToAchievements = onNavigateToAchievements,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToSkillDetail = onNavigateToSkillDetail,
                onNavigateBack = onNavigateBack
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
    onNavigateToSkillDetail: (com.aivoicepower.domain.model.user.SkillType) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    // Track if skills block has become visible (animate once, then stay)
    var skillsBarsAnimated by remember { mutableStateOf(false) }

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
            modifier = Modifier.staggeredEntry(index = 0)
        )

        // Combined Progress Overview
        ProgressOverviewCard(
            overallLevel = state.overallLevel,
            currentStreak = state.currentStreak,
            longestStreak = state.longestStreak,
            totalExercises = state.totalExercises,
            totalMinutes = state.totalMinutes,
            totalRecordings = state.totalRecordings,
            modifier = Modifier.staggeredEntry(index = 1)
        )

        // Journey Section
        JourneySection(
            overallLevel = state.overallLevel,
            modifier = Modifier.staggeredEntry(index = 2)
        )

        // Skill Levels
        Text(
            text = "Навички",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.staggeredEntry(index = 3)
        )

        Column(
            modifier = Modifier
                .staggeredEntry(index = 4)
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.18f),
                    ambientColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(20.dp)
                .onGloballyPositioned { coordinates ->
                    if (!skillsBarsAnimated) {
                        val posY = coordinates.positionInRoot().y
                        val height = coordinates.size.height
                        // Trigger when at least part of the card is visible
                        if (posY < screenHeightPx && posY + height > 0) {
                            skillsBarsAnimated = true
                        }
                    }
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Radar Chart
            SkillRadarChart(
                skillLevels = state.skillLevels,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            )

            // Helper text for interactivity
            Text(
                text = "\uD83D\uDCA1 Натисніть на навичку, щоб дізнатися більше",
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            // Bar Chart — animates when scrolled into view, stays fixed after
            SkillBarChart(
                skillLevels = state.skillLevels,
                onSkillClick = onNavigateToSkillDetail,
                animateBars = skillsBarsAnimated
            )
        }

        // Recent Achievements
        if (state.recentAchievements.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .staggeredEntry(index = 5)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 0.18f)
                    )
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onNavigateToAchievements()
                    }
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Досягнення",
                        style = AppTypography.titleLarge,
                        color = TextColors.onLightPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${state.unlockedAchievements}/${state.totalAchievements}",
                            style = AppTypography.bodyMedium,
                            color = Color(0xFF667EEA),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF667EEA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                state.recentAchievements.forEach { achievement ->
                    AchievementBadge(
                        achievement = achievement,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Quick Actions
        Text(
            text = "Дії",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.staggeredEntry(index = 7)
        )

        ActionCard(
            icon = Icons.Default.Compare,
            title = "Порівняти з початком",
            onClick = onNavigateToCompare,
            modifier = Modifier.staggeredEntry(index = 8)
        )

        ActionCard(
            icon = Icons.Default.History,
            title = "Історія записів",
            onClick = onNavigateToHistory,
            modifier = Modifier.staggeredEntry(index = 9)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProgressHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
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
            .scaleOnPress(pressedScale = 0.97f)
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


package com.aivoicepower.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.model.user.AchievementCategory
import com.aivoicepower.ui.screens.progress.components.AchievementBadge
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
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
                    CircularProgressIndicator(color = Color.White)
                }
            }

            else -> {
                AchievementsContent(
                    state = state,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun AchievementsContent(
    state: AchievementsState,
    onNavigateBack: () -> Unit
) {
    val grouped = state.achievements.groupBy { it.category }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Досягнення",
                    style = AppTypography.displayLarge,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp
                )
                Text(
                    text = "${state.unlockedCount}/${state.totalCount} відкрито",
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Overall progress card
        item {
            Box(
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
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Загальний прогрес",
                        style = AppTypography.titleMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = {
                            if (state.totalCount > 0) state.unlockedCount.toFloat() / state.totalCount
                            else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = Color(0xFF6366F1),
                        trackColor = Color(0xFFE5E7EB)
                    )
                }
            }
        }

        // Categories
        val categoryOrder = listOf(
            AchievementCategory.SPECIAL,
            AchievementCategory.STREAK,
            AchievementCategory.RECORDINGS,
            AchievementCategory.PRACTICE_TIME
        )

        categoryOrder.forEach { category ->
            val achievements = grouped[category] ?: return@forEach

            val categoryIcon = when (category) {
                AchievementCategory.STREAK -> "\uD83D\uDD25"
                AchievementCategory.PRACTICE_TIME -> "⏱\uFE0F"
                AchievementCategory.RECORDINGS -> "\uD83C\uDF99\uFE0F"
                AchievementCategory.SPECIAL -> "✨"
            }

            item {
                Text(
                    text = "$categoryIcon ${category.displayName}",
                    style = AppTypography.titleLarge,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            }

            item {
                AchievementCategoryGrid(achievements = achievements)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AchievementCategoryGrid(
    achievements: List<Achievement>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Grid in rows of 2
        achievements.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { achievement ->
                    AchievementBadge(
                        achievement = achievement,
                        isLarge = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

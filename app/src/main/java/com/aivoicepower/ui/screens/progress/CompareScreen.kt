package com.aivoicepower.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.progress.components.ComparisonMetricCard
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import kotlin.math.abs

@Composable
fun CompareScreen(
    viewModel: CompareViewModel = hiltViewModel(),
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

            state.initialDiagnostic == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompareHeader(onNavigateBack = onNavigateBack)

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
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
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Недостатньо даних",
                                style = AppTypography.headlineSmall,
                                color = TextColors.onLightPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Пройдіть діагностику та зробіть кілька вправ",
                                style = AppTypography.bodyMedium,
                                color = TextColors.onLightSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        CompareHeader(onNavigateBack = onNavigateBack)
                    }

                    // Hero Progress Card with Gradient
                    item {
                        HeroProgressCard(
                            initialLevel = state.initialDiagnostic?.calculateOverallLevel() ?: 0,
                            currentLevel = state.currentLevel,
                            improvement = state.improvement
                        )
                    }

                    // Insights & Motivation
                    item {
                        InsightsCard(
                            improvement = state.improvement,
                            comparisons = state.comparisons
                        )
                    }

                    // Achievement Badges
                    item {
                        AchievementBadges(
                            improvement = state.improvement,
                            comparisons = state.comparisons
                        )
                    }

                    // Section Header
                    item {
                        Text(
                            text = "Деталізація по навичках",
                            style = AppTypography.titleLarge,
                            color = TextColors.onDarkPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Enhanced Skill Comparison Cards
                    itemsIndexed(state.comparisons) { _, comparison ->
                        EnhancedComparisonCard(
                            skillName = comparison.skillName,
                            initialValue = comparison.initialValue,
                            currentValue = comparison.currentValue,
                            improvement = comparison.improvement
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroProgressCard(
    initialLevel: Int,
    currentLevel: Int,
    improvement: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.3f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6366F1),
                        Color(0xFF8B5CF6)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(28.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "🎯 Загальний прогрес",
                style = AppTypography.titleLarge,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Старт",
                        style = AppTypography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$initialLevel",
                            style = AppTypography.headlineMedium,
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Зараз",
                        style = AppTypography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currentLevel",
                            style = AppTypography.headlineMedium,
                            color = Color(0xFF6366F1),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(32.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Зріст",
                        style = AppTypography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFF10B981), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$improvement%",
                            style = AppTypography.headlineMedium,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightsCard(
    improvement: Int,
    comparisons: List<SkillComparison>
) {
    val topSkill = comparisons.maxByOrNull { it.improvement }
    val message = when {
        improvement >= 50 -> "🏆 Фантастичний прогрес! Ви зробили величезний крок вперед!"
        improvement >= 30 -> "⭐ Чудова робота! Ваші зусилля помітні!"
        improvement >= 15 -> "👏 Відмінно! Продовжуйте в тому ж дусі!"
        improvement >= 5 -> "💪 Хороший старт! Ви на правильному шляху!"
        else -> "🎯 Почніть з вправ для швидшого прогресу"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "💡 Ваші досягнення",
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = message,
            style = AppTypography.bodyLarge,
            color = TextColors.onLightSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp
        )

        if (topSkill != null && topSkill.improvement > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F9FF), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = "Найбільший прогрес",
                        style = AppTypography.labelSmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${topSkill.skillName}: +${topSkill.improvement}%",
                        style = AppTypography.bodyLarge,
                        color = Color(0xFF0284C7),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementBadges(
    improvement: Int,
    comparisons: List<SkillComparison>
) {
    val badges = mutableListOf<Pair<String, String>>()

    if (improvement >= 20) badges.add("🌟" to "Швидке зростання")
    if (comparisons.count { it.improvement >= 25 } >= 3) badges.add("🎯" to "Всебічний розвиток")
    if (comparisons.any { it.improvement >= 50 }) badges.add("🚀" to "Експерт")
    if (improvement >= 10) badges.add("💎" to "Постійність")

    if (badges.isEmpty()) badges.add("🎓" to "Початківець")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🏅 Досягнення",
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            badges.forEach { (emoji, title) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFEF3C7),
                                    Color(0xFFFEF3C7)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = emoji,
                        fontSize = 32.sp
                    )
                    Text(
                        text = title,
                        style = AppTypography.labelSmall,
                        color = Color(0xFF92400E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedComparisonCard(
    skillName: String,
    initialValue: Int,
    currentValue: Int,
    improvement: Int
) {
    val progressColor = when {
        improvement >= 30 -> Color(0xFF10B981)
        improvement >= 15 -> Color(0xFF3B82F6)
        improvement >= 5 -> Color(0xFF8B5CF6)
        else -> Color(0xFF94A3B8)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = skillName,
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Було",
                        style = AppTypography.labelSmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$initialValue",
                        style = AppTypography.bodyLarge,
                        color = TextColors.onLightMuted,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "Зараз",
                        style = AppTypography.labelSmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currentValue",
                        style = AppTypography.bodyLarge,
                        color = Color(0xFF667EEA),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Прогрес",
                        style = AppTypography.labelSmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currentValue / 100",
                        style = AppTypography.labelSmall,
                        color = progressColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = { currentValue / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = progressColor,
                    trackColor = Color(0xFFE5E7EB)
                )
            }
        }

        if (improvement > 0) {
            Row(
                modifier = Modifier
                    .background(progressColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = progressColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "+$improvement%",
                    style = AppTypography.titleMedium,
                    color = progressColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun CompareHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Твій розвиток",
                style = AppTypography.labelMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Порівняти з початком",
                style = AppTypography.displayLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp
            )
        }

        // Back button
        Row(
            modifier = Modifier
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                .background(Color.White, RoundedCornerShape(16.dp))
                .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onNavigateBack() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "←", fontSize = 24.sp, color = Color(0xFF667EEA), fontWeight = FontWeight.Bold)
            Text(text = "Назад", style = AppTypography.bodyMedium, color = TextColors.onLightPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "← Назад",
            style = AppTypography.bodyLarge,
            color = Color(0xFF667EEA),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

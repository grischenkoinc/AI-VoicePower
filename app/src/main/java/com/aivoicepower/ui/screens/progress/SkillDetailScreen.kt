package com.aivoicepower.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.domain.model.user.toDisplayString
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import kotlin.math.max

@Composable
fun SkillDetailScreen(
    viewModel: SkillDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
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
            SkillDetailContent(
                state = state,
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@Composable
private fun SkillDetailContent(
    state: SkillDetailState,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header with back button and skill name
        SkillHeader(
            skillName = state.skillType.toDisplayString(),
            currentLevel = state.currentLevel,
            onNavigateBack = onNavigateBack
        )

        // Level Progress Card
        LevelProgressCard(
            currentLevel = state.currentLevel,
            initialLevel = state.initialLevel
        )

        // History Chart Card
        if (state.historyPoints.isNotEmpty()) {
            HistoryChartCard(historyPoints = state.historyPoints)
        }

        // Impactful Exercises Card
        if (state.impactfulExercises.isNotEmpty()) {
            ImpactfulExercisesCard(exercises = state.impactfulExercises)
        }

        // Recommendations Card
        if (state.recommendations.isNotEmpty()) {
            RecommendationsCard(recommendations = state.recommendations)
        }

        // Stats Card
        DetailedStatsCard(
            totalPracticeMinutes = state.totalPracticeMinutes,
            recordingCount = state.impactfulExercises.sumOf { it.completionCount }
        )
    }
}

@Composable
private fun SkillHeader(
    skillName: String,
    currentLevel: Int,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = skillName,
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
        }

        // Circular progress indicator
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            Canvas(modifier = Modifier.size(100.dp)) {
                val sweepAngle = (currentLevel / 100f) * 360f

                // Background circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )

                // Progress arc
                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Text(
                text = "$currentLevel",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun LevelProgressCard(
    currentLevel: Int,
    initialLevel: Int
) {
    val change = currentLevel - initialLevel
    val changePercent = if (initialLevel > 0) ((change.toFloat() / initialLevel) * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF6366F1).copy(alpha = 0.3f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color(0xFF6366F1),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Прогрес навички",
                style = AppTypography.titleLarge,
                color = TextColors.onLightPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Початковий",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "$initialLevel",
                    style = AppTypography.headlineMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = if (change >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.size(32.dp)
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Поточний",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "$currentLevel",
                    style = AppTypography.headlineMedium,
                    color = Color(0xFF6366F1),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        if (change != 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (change >= 0) Color(0xFF10B981).copy(alpha = 0.1f)
                        else Color(0xFFEF4444).copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (change >= 0) "↗ +$change балів (+$changePercent%)"
                    else "↘ $change балів ($changePercent%)",
                    style = AppTypography.bodyMedium,
                    color = if (change >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HistoryChartCard(historyPoints: List<SkillHistoryPoint>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📊 Історія змін",
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        // Simple line chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val maxLevel = max(historyPoints.maxOf { it.level }, 100)
            val width = size.width - 40f
            val height = size.height - 40f
            val stepX = width / (historyPoints.size - 1).coerceAtLeast(1)

            val linePath = Path()
            historyPoints.forEachIndexed { index, point ->
                val x = 20f + index * stepX
                val y = 20f + (height - (point.level.toFloat() / maxLevel * height))

                if (index == 0) {
                    linePath.moveTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                }
            }

            // Draw line
            drawPath(
                path = linePath,
                color = Color(0xFF6366F1),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            // Draw points
            historyPoints.forEachIndexed { index, point ->
                val x = 20f + index * stepX
                val y = 20f + (height - (point.level.toFloat() / maxLevel * height))

                drawCircle(
                    color = Color(0xFF6366F1).copy(alpha = 0.3f),
                    radius = 8f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color(0xFF6366F1),
                    radius = 5f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
private fun ImpactfulExercisesCard(exercises: List<ExerciseImpact>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "✍️ Цю навичку покращили",
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        exercises.forEach { exercise ->
            ExerciseImpactItem(exercise)
        }
    }
}

@Composable
private fun ExerciseImpactItem(exercise: ExerciseImpact) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.exerciseName,
                style = AppTypography.bodyLarge,
                color = TextColors.onLightPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Виконано ${exercise.completionCount} разів",
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .background(
                    Color(0xFF6366F1).copy(alpha = 0.1f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "+${exercise.impactScore}",
                style = AppTypography.labelMedium,
                color = Color(0xFF6366F1),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecommendationsCard(recommendations: List<SkillRecommendation>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "💡 Як покращити далі",
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        recommendations.forEach { recommendation ->
            RecommendationItem(recommendation)
        }
    }
}

@Composable
private fun RecommendationItem(recommendation: SkillRecommendation) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = 6.dp)
                .background(Color(0xFF6366F1), CircleShape)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recommendation.tip,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )

            if (recommendation.exerciseName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 ${recommendation.exerciseName}",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF6366F1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DetailedStatsCard(
    totalPracticeMinutes: Int,
    recordingCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📈 Детальна статистика",
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(
                label = "Час практики",
                value = "${totalPracticeMinutes}хв",
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "Всього записів",
                value = "$recordingCount",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = AppTypography.headlineMedium,
            color = Color(0xFF6366F1),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = TextColors.onLightSecondary,
            fontSize = 12.sp
        )
    }
}

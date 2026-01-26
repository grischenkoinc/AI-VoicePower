package com.aivoicepower.ui.screens.diagnostic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DiagnosticResultScreen(
    onContinue: () -> Unit,
    viewModel: DiagnosticResultViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground(content = {})

        when (val state = uiState) {
            is DiagnosticResultUiState.Loading -> {
                LoadingState()
            }
            is DiagnosticResultUiState.Success -> {
                SuccessContent(
                    result = state.result,
                    onContinue = onContinue
                )
            }
            is DiagnosticResultUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadResults() }
                )
            }
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp),
                color = Color.White,
                strokeWidth = 5.dp
            )
            Text(
                text = "Завантаження результатів...",
                style = AppTypography.bodyMedium,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
                text = message,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            PrimaryButton(
                text = "Спробувати знову",
                onClick = onRetry
            )
        }
    }
}

@Composable
private fun SuccessContent(
    result: DiagnosticResult,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        HeaderSection(overallScore = result.overallScore)

        // Radar Chart
        RadarChartCard(
            dictionScore = result.dictionScore,
            tempoScore = result.tempoScore,
            intonationScore = result.intonationScore,
            structureScore = result.structureScore,
            confidenceScore = result.confidenceScore,
            fillerWordsScore = result.fillerWordsScore
        )

        // Detailed Metrics
        DetailedMetricsSection(result = result)

        // Strengths
        if (result.strengths.isNotEmpty()) {
            FeedbackCard(
                title = "Ваші сильні сторони",
                icon = "💪",
                items = result.strengths,
                color = Color(0xFF10B981)
            )
        }

        // Areas to Improve
        if (result.areasToImprove.isNotEmpty()) {
            FeedbackCard(
                title = "Що покращити",
                icon = "🎯",
                items = result.areasToImprove,
                color = Color(0xFFF59E0B)
            )
        }

        // Recommendations
        if (result.recommendations.isNotEmpty()) {
            FeedbackCard(
                title = "Рекомендації AI-коуча",
                icon = "✨",
                items = result.recommendations,
                color = Color(0xFF667EEA)
            )
        }

        // Continue Button
        PrimaryButton(
            text = "Почати навчання",
            onClick = onContinue
        )
    }
}

@Composable
private fun HeaderSection(
    overallScore: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "🎉", fontSize = 64.sp)

        Text(
            text = "Діагностика завершена!",
            style = AppTypography.displayLarge,
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            letterSpacing = (-1.2).sp
        )

        // Overall Score Badge
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = Color.White.copy(alpha = 0.3f)
                )
                .background(Color.White, CircleShape)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Загальний рівень",
                    style = AppTypography.labelMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$overallScore",
                    style = AppTypography.displayLarge,
                    color = Color(0xFF667EEA),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = getScoreMessage(overallScore),
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RadarChartCard(
    dictionScore: Int,
    tempoScore: Int,
    intonationScore: Int,
    structureScore: Int,
    confidenceScore: Int,
    fillerWordsScore: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
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
        Text(
            text = "Аналіз навичок",
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )

        RadarChart(
            metrics = listOf(
                RadarMetric("Дикція", dictionScore),
                RadarMetric("Темп", tempoScore),
                RadarMetric("Інтонація", intonationScore),
                RadarMetric("Структура", structureScore),
                RadarMetric("Впевненість", confidenceScore),
                RadarMetric("Без паразитів", fillerWordsScore)
            )
        )
    }
}

@Composable
private fun RadarChart(
    metrics: List<RadarMetric>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas з КРУГЛИМ радаром
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.85f
            val angleStep = 360f / metrics.size

            // Background CIRCLES замість шестикутників
            for (i in 1..5) {
                val circleRadius = radius * i / 5
                drawCircle(
                    color = Color(0xFFE5E7EB),
                    radius = circleRadius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }

            // Axes (6 ліній до країв)
            metrics.forEachIndexed { index, _ ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val endX = center.x + (radius * cos(angle)).toFloat()
                val endY = center.y + (radius * sin(angle)).toFloat()

                drawLine(
                    color = Color(0xFFE5E7EB),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }

            // Data polygon
            val dataPath = Path()
            metrics.forEachIndexed { index, metric ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val distance = radius * (metric.value / 100f)
                val x = center.x + (distance * cos(angle)).toFloat()
                val y = center.y + (distance * sin(angle)).toFloat()

                if (index == 0) dataPath.moveTo(x, y)
                else dataPath.lineTo(x, y)
            }
            dataPath.close()

            drawPath(path = dataPath, color = Color(0xFF667EEA).copy(alpha = 0.3f))
            drawPath(path = dataPath, color = Color(0xFF667EEA), style = Stroke(width = 3f))

            // Points
            metrics.forEachIndexed { index, metric ->
                val angle = Math.toRadians((angleStep * index - 90).toDouble())
                val distance = radius * (metric.value / 100f)
                val x = center.x + (distance * cos(angle)).toFloat()
                val y = center.y + (distance * sin(angle)).toFloat()

                drawCircle(color = Color(0xFF667EEA), radius = 6f, center = Offset(x, y))
                drawCircle(color = Color.White, radius = 3f, center = Offset(x, y))
            }
        }

        // Мітки з компенсацією розміру
        val labelDistance = 118.dp // Відстань від центру до кінця осі
        val angleStep = 360f / metrics.size

        metrics.forEachIndexed { index, metric ->
            val angleDegrees = angleStep * index - 90
            val angleRadians = Math.toRadians(angleDegrees.toDouble())

            // Базовий offset від центру
            val baseOffsetX = (labelDistance.value * cos(angleRadians)).dp
            val baseOffsetY = (labelDistance.value * sin(angleRadians)).dp

            // Розмір мітки (для компенсації)
            val labelWidth = if (metric.label == "Без паразитів") 75.dp else 65.dp
            val labelHeight = if (metric.label == "Без паразитів") 40.dp else 30.dp

            // Компенсація залежно від кута
            val correctionX = when {
                angleDegrees in -30f..30f -> 0.dp // Top-Right area
                angleDegrees in 30f..90f -> -(labelWidth / 2) // Right
                angleDegrees in 90f..150f -> -(labelWidth / 2) // Bottom-Right
                angleDegrees in 150f..210f -> 0.dp // Bottom
                angleDegrees in 210f..270f -> (labelWidth / 2) // Bottom-Left
                else -> (labelWidth / 2) // Left/Top-Left
            }

            val correctionY = when {
                angleDegrees in -90f..-30f -> (labelHeight / 2) // Top
                angleDegrees in -30f..30f -> 0.dp // Top-Right
                angleDegrees in 30f..90f -> -(labelHeight / 2) // Right
                angleDegrees in 90f..150f -> -(labelHeight / 2) // Bottom-Right
                angleDegrees in 150f..210f -> -(labelHeight / 2) // Bottom
                else -> 0.dp // Left area
            }

            RadarLabel(
                label = metric.label,
                value = metric.value,
                isLong = metric.label == "Без паразитів",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = baseOffsetX + correctionX,
                        y = baseOffsetY + correctionY
                    )
            )
        }
    }
}

@Composable
private fun RadarLabel(
    label: String,
    value: Int,
    isLong: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(if (isLong) 75.dp else 65.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = TextColors.onLightSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = if (isLong) 2 else 1,
            lineHeight = if (isLong) 12.sp else 10.sp,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "$value",
            style = AppTypography.bodyMedium,
            color = Color(0xFF667EEA),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun DetailedMetricsSection(
    result: DiagnosticResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            icon = "🎯",
            title = "Чіткість дикції",
            description = "Артикуляція звуків, чіткість вимови",
            score = result.dictionScore,
            color = Color(0xFF6366F1)
        )

        MetricCard(
            icon = "⚡",
            title = "Темп мовлення",
            description = "Швидкість та ритмічність",
            score = result.tempoScore,
            color = Color(0xFFEC4899)
        )

        MetricCard(
            icon = "🎭",
            title = "Інтонація та виразність",
            description = "Емоційність, модуляція голосу",
            score = result.intonationScore,
            color = Color(0xFFF59E0B)
        )

        MetricCard(
            icon = "🧩",
            title = "Структура думок",
            description = "Логічність, послідовність викладу",
            score = result.structureScore,
            color = Color(0xFF10B981)
        )

        MetricCard(
            icon = "💪",
            title = "Впевненість",
            description = "Стабільність голосу, відсутність тремтіння",
            score = result.confidenceScore,
            color = Color(0xFF8B5CF6)
        )

        MetricCard(
            icon = "✨",
            title = "Без слів-паразитів",
            description = "Відсутність 'е-е', 'ну', 'типу'",
            score = result.fillerWordsScore,
            color = Color(0xFF14B8A6)
        )
    }
}

@Composable
private fun MetricCard(
    icon: String,
    title: String,
    description: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = color.copy(alpha = 0.3f)
                    )
                    .background(color, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = description,
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFFE5E5EA), RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(score / 100f)
                            .fillMaxHeight()
                            .background(color, RoundedCornerShape(3.dp))
                    )
                }
            }

            Text(
                text = "$score",
                style = AppTypography.titleLarge,
                color = color,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun FeedbackCard(
    title: String,
    icon: String,
    items: List<String>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(28.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 32.sp)
            Text(
                text = title,
                style = AppTypography.titleLarge,
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { item ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(color.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "•",
                            color = color,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = item,
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                ),
                RoundedCornerShape(20.dp)
            )
            .scaleOnPress()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.titleMedium,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun getScoreMessage(score: Int): String = when {
    score >= 90 -> "Чудово! 🌟"
    score >= 80 -> "Дуже добре! 💪"
    score >= 70 -> "Добре! 👍"
    score >= 60 -> "Непогано! 📈"
    else -> "Є над чим працювати! 💪"
}

data class RadarMetric(
    val label: String,
    val value: Int
)

package com.aivoicepower.ui.screens.diagnostic

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicepower.ui.components.ReportAiContentDialog
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*
import com.aivoicepower.utils.AnalyticsTracker

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(onClick = onRetry),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Спробувати знову",
                    style = AppTypography.titleMedium,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
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
    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog) {
        ReportAiContentDialog(
            onDismiss = { showReportDialog = false },
            onReport = { reason ->
                AnalyticsTracker().logAiContentReported("diagnostic_result", reason)
                showReportDialog = false
            }
        )
    }

    // === REVEAL ORCHESTRATION ===
    var showHeader by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var showSections by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        showHeader = true
        kotlinx.coroutines.delay(300)
        showScore = true
        kotlinx.coroutines.delay(1200)
        showSections = true
        kotlinx.coroutines.delay(600)
        showButton = true
    }

    // === SCORE ANIMATIONS ===
    val scoreColor = when (result.overallScore) {
        in 0..39 -> Color(0xFFEF4444)
        in 40..69 -> Color(0xFFF59E0B)
        else -> Color(0xFF22C55E)
    }

    val animatedScore by animateIntAsState(
        targetValue = if (showScore) result.overallScore else 0,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "scoreCountUp"
    )

    val circleScale by animateFloatAsState(
        targetValue = if (showScore) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "circleScale"
    )

    val headerScale by animateFloatAsState(
        targetValue = if (showHeader) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "headerScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // === HEADER ===
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.graphicsLayer {
                scaleX = headerScale
                scaleY = headerScale
                alpha = headerScale
            }
        ) {
            Text(text = "🎉", fontSize = 56.sp)

            Text(
                text = "Діагностика\nзавершена!",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                letterSpacing = (-1).sp,
                lineHeight = 38.sp
            )
        }

        // === RESULTS CARD ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2))),
                    shape = RoundedCornerShape(24.dp)
                )
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated overall score circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX = circleScale
                        scaleY = circleScale
                    }
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = scoreColor.copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(scoreColor, scoreColor.copy(alpha = 0.8f))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$animatedScore",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 34.sp
                    )
                    Text(
                        text = "/100",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = getScoreMessage(result.overallScore),
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            // === METRICS ===
            val metrics = listOf(
                "Дикція" to result.dictionScore,
                "Темп" to result.tempoScore,
                "Інтонація" to result.intonationScore,
                "Структура" to result.structureScore,
                "Впевненість" to result.confidenceScore,
                "Без слів-паразитів" to result.fillerWordsScore
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                metrics.forEachIndexed { index, (label, score) ->
                    DiagnosticMetricBar(
                        label = label,
                        score = score,
                        animationDelay = index * 100
                    )
                }
            }

            // === STRENGTHS ===
            if (result.strengths.isNotEmpty()) {
                AnimatedVisibility(
                    visible = showSections,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFF22C55E).copy(alpha = 0.08f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "💪 Сильні сторони",
                            style = AppTypography.labelMedium,
                            color = Color(0xFF16A34A),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        result.strengths.forEach { strength ->
                            Text(
                                text = "• $strength",
                                style = AppTypography.bodySmall,
                                color = TextColors.onLightPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // === IMPROVEMENTS ===
            if (result.areasToImprove.isNotEmpty()) {
                AnimatedVisibility(
                    visible = showSections,
                    enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(
                        animationSpec = tween(400, delayMillis = 200, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF59E0B).copy(alpha = 0.08f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🎯 Що покращити",
                            style = AppTypography.labelMedium,
                            color = Color(0xFFD97706),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        result.areasToImprove.forEach { improvement ->
                            Text(
                                text = "• $improvement",
                                style = AppTypography.bodySmall,
                                color = TextColors.onLightPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // === RECOMMENDATIONS ===
            if (result.recommendations.isNotEmpty()) {
                AnimatedVisibility(
                    visible = showSections,
                    enter = fadeIn(tween(400, delayMillis = 400)) + slideInVertically(
                        animationSpec = tween(400, delayMillis = 400, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFF667EEA).copy(alpha = 0.08f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "✨ Рекомендації AI-коуча",
                            style = AppTypography.labelMedium,
                            color = Color(0xFF667EEA),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        result.recommendations.forEach { recommendation ->
                            Text(
                                text = "• $recommendation",
                                style = AppTypography.bodySmall,
                                color = TextColors.onLightPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // === CONTINUE BUTTON ===
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(tween(300)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .scaleOnPress()
                    .clickable(onClick = onContinue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Почати навчання",
                    style = AppTypography.titleMedium,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Report AI content link
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(tween(300, delayMillis = 200))
        ) {
            Text(
                text = "Поскаржитись на AI-відповідь",
                style = AppTypography.bodySmall,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { showReportDialog = true }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun DiagnosticMetricBar(
    label: String,
    score: Int,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) score / 100f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "metricProgress_$label"
    )

    val barColor = when (score) {
        in 0..39 -> Color(0xFFEF4444)
        in 40..69 -> Color(0xFFF59E0B)
        else -> Color(0xFF22C55E)
    }

    val barGradient = when (score) {
        in 0..39 -> Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFF87171)))
        in 40..69 -> Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)))
        else -> Brush.horizontalGradient(listOf(Color(0xFF22C55E), Color(0xFF4ADE80)))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$score",
                style = AppTypography.titleLarge,
                color = barColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(10.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(barGradient, RoundedCornerShape(5.dp))
            )
        }
    }
}

private fun getScoreMessage(score: Int): String = when {
    score >= 90 -> "Чудово!"
    score >= 80 -> "Дуже добре!"
    score >= 70 -> "Добре!"
    score >= 60 -> "Непогано!"
    else -> "Є над чим працювати!"
}

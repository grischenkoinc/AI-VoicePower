package com.aivoicepower.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.utils.AnalyticsTracker

@Composable
fun AnalyzingContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "analyzing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // AI icon with pulse
        Box(
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFF667EEA).copy(alpha = 0.5f)
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✨", fontSize = 40.sp)
        }

        Text(
            text = "Аналізую ваше мовлення...",
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        // Loading dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(3) { index ->
                val dotTransition = rememberInfiniteTransition(label = "dot_$index")
                val dotAlpha by dotTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = index * 200),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dotAlpha_$index"
                )

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            Color(0xFF667EEA).copy(alpha = dotAlpha),
                            CircleShape
                        )
                )
            }
        }

        Text(
            text = "AI-тренер оцінює вашу дикцію",
            style = AppTypography.bodyMedium,
            color = TextColors.onLightSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AnalysisResultsContent(
    result: VoiceAnalysisResult,
    onDismiss: () -> Unit,
    dismissButtonText: String = "Готово",
    onRetry: (() -> Unit)? = null
) {
    val view = LocalView.current
    val context = LocalContext.current
    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog) {
        ReportAiContentDialog(
            onDismiss = { showReportDialog = false },
            onReport = { reason ->
                AnalyticsTracker(context.applicationContext).logAiContentReported("analysis_result", reason)
                showReportDialog = false
            }
        )
    }
    // === REVEAL ORCHESTRATION ===
    var showScore by remember { mutableStateOf(false) }
    var showSections by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        showScore = true
        kotlinx.coroutines.delay(1200)
        showSections = true
        kotlinx.coroutines.delay(600)
        showButtons = true
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

        // Metrics — show all scored criteria
        val metrics = buildList {
            add("Дикція" to result.diction)
            add("Темп" to result.tempo)
            add("Інтонація" to result.intonation)
            result.emotionality?.let { if (it > 0) add("Емоційність" to it) }
            if (result.confidence > 0) add("Впевненість" to result.confidence)
            if (result.fillerWords > 0) add("Без слів-паразитів" to result.fillerWords)
            if (result.structure > 0) add("Структура" to result.structure)
            if (result.persuasiveness > 0) add("Переконливість" to result.persuasiveness)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            metrics.forEachIndexed { index, (label, score) ->
                AnalysisMetricBar(
                    label = label,
                    score = score,
                    animationDelay = index * 100
                )
            }
        }

        // Strengths (animated)
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

        // Improvements (animated)
        if (result.improvements.isNotEmpty()) {
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
                    result.improvements.forEach { improvement ->
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

        // Tip (animated)
        if (result.tip.isNotEmpty()) {
            AnimatedVisibility(
                visible = showSections,
                enter = fadeIn(tween(400, delayMillis = 400)) + slideInVertically(
                    animationSpec = tween(400, delayMillis = 400, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF667EEA).copy(alpha = 0.08f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "💡", fontSize = 18.sp)
                    Text(
                        text = result.tip,
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Coach comment (animated)
        if (result.coachComment.isNotEmpty()) {
            AnimatedVisibility(
                visible = showSections,
                enter = fadeIn(tween(400, delayMillis = 600)) + slideInVertically(
                    animationSpec = tween(400, delayMillis = 600, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 4 }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF8B5CF6).copy(alpha = 0.08f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "\uD83C\uDFCB\uFE0F", fontSize = 18.sp)
                    Text(
                        text = result.coachComment,
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Retry button (animated)
        if (onRetry != null && result.overallScore == 0) {
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(tween(300)) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color(0xFF10B981).copy(alpha = 0.3f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF10B981), Color(0xFF059669))
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onRetry() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Перезаписати",
                        style = AppTypography.bodyLarge,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Done/Next button (animated)
        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn(tween(300)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        ) {
            val isSecondary = onRetry != null && result.overallScore == 0
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = if (isSecondary) Color(0xFF6B7280).copy(alpha = 0.3f)
                        else Color(0xFF667EEA).copy(alpha = 0.3f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = if (isSecondary) listOf(Color(0xFF9CA3AF), Color(0xFF6B7280))
                            else listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onDismiss() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dismissButtonText,
                    style = AppTypography.bodyLarge,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Report AI content link
        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn(tween(300, delayMillis = 200))
        ) {
            Text(
                text = "Поскаржитись на AI-відповідь",
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { showReportDialog = true }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun AnalysisMetricBar(
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

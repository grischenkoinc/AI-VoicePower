package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun VoiceExerciseScreen(
    title: String,
    stepInfo: String,
    roleEmoji: String,
    roleName: String,
    aiText: String,
    hint: String? = null,
    orbState: OrbState,
    audioLevel: Float = 0f,
    isRecording: Boolean = false,
    recordingSeconds: Int = 0,
    onRecordClick: () -> Unit,
    onStopClick: () -> Unit = {},
    onBackClick: () -> Unit,
    onAnalyzeClick: (() -> Unit)? = null,
    onSkipClick: (() -> Unit)? = null,
    errorMessage: String? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // === TOP BAR ===
            VoiceExerciseTopBar(
                title = title,
                stepInfo = stepInfo,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // === ROLE BADGE ===
            RoleBadge(emoji = roleEmoji, name = roleName)

            // === SCROLLABLE MIDDLE CONTENT ===
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // === AI TEXT ===
                AnimatedVisibility(
                    visible = aiText.isNotBlank(),
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -20 },
                    exit = fadeOut(tween(200))
                ) {
                    AiTextBubble(text = aiText)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // === ANIMATED ORB ===
                AnimatedOrb(
                    state = orbState,
                    emoji = roleEmoji,
                    audioLevel = audioLevel,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(16.dp))

                // === RECORDING TIMER ===
                AnimatedVisibility(
                    visible = isRecording,
                    enter = fadeIn(tween(300)) + scaleIn(tween(300)),
                    exit = fadeOut(tween(200)) + scaleOut(tween(200))
                ) {
                    RecordingTimer(seconds = recordingSeconds)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // === FIXED BOTTOM SECTION ===

            // === HINT ===
            hint?.let {
                AnimatedVisibility(
                    visible = orbState == OrbState.IDLE || orbState == OrbState.LISTENING,
                    enter = fadeIn(tween(400)),
                    exit = fadeOut(tween(200))
                ) {
                    HintText(text = it)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === COMPLETION BUTTONS or RECORD BUTTON ===
            if (orbState == OrbState.COMPLETE && onAnalyzeClick != null && onSkipClick != null) {
                CompletionButtons(
                    onAnalyzeClick = onAnalyzeClick,
                    onSkipClick = onSkipClick
                )
            } else {
                VoiceActionButton(
                    isRecording = isRecording,
                    orbState = orbState,
                    audioLevel = audioLevel,
                    onRecordClick = onRecordClick,
                    onStopClick = onStopClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === ERROR ===
            errorMessage?.let {
                ErrorBanner(message = it)
            }
        }
    }
}

@Composable
private fun VoiceExerciseTopBar(
    title: String,
    stepInfo: String,
    onBackClick: () -> Unit
) {
    val view = LocalView.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }

        // Title
        Text(
            text = title,
            style = AppTypography.titleMedium,
            color = TextColors.onDarkPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        // Step info
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = stepInfo,
                style = AppTypography.labelMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RoleBadge(emoji: String, name: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Text(
            text = name,
            style = AppTypography.labelMedium,
            color = TextColors.onDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AiTextBubble(text: String) {
    // Видаляємо markdown та структурні мітки з тексту AI
    val cleanText = text
        .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")  // **bold** → bold
        .replace(Regex("\\*(.+?)\\*"), "$1")          // *italic* → italic
        .replace(Regex("__(.+?)__"), "$1")             // __underline__ → underline
        .replace(Regex("^Реакція:?\\s*", RegexOption.MULTILINE), "")
        .replace(Regex("^Питання:?\\s*", RegexOption.MULTILINE), "")
        .replace(Regex("^Раунд \\d+ з \\d+\\.?\\s*", RegexOption.MULTILINE), "")
        .trim()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = cleanText,
            style = AppTypography.bodyLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RecordingTimer(seconds: Int) {
    val minutes = seconds / 60
    val secs = seconds % 60

    val infiniteTransition = rememberInfiniteTransition(label = "rec_dot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pulsing red dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer { alpha = dotAlpha }
                .background(Color(0xFFEF4444), CircleShape)
        )
        Text(
            text = String.format("%d:%02d", minutes, secs),
            style = AppTypography.titleMedium,
            color = Color(0xFFEF4444),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HintText(text: String) {
    Text(
        text = text,
        style = AppTypography.bodySmall,
        color = TextColors.onDarkMuted,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun VoiceActionButton(
    isRecording: Boolean,
    orbState: OrbState,
    audioLevel: Float,
    onRecordClick: () -> Unit,
    onStopClick: () -> Unit
) {
    val view = LocalView.current
    val enabled = orbState == OrbState.IDLE || orbState == OrbState.LISTENING

    val infiniteTransition = rememberInfiniteTransition(label = "btn_pulse")
    val buttonPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_pulse"
    )

    val buttonScale = when {
        isRecording -> 1f + audioLevel * 0.1f
        orbState == OrbState.IDLE -> buttonPulse
        else -> 1f
    }

    val buttonColors = if (isRecording) {
        listOf(Color(0xFFEF4444), Color(0xFFDC2626))
    } else {
        listOf(Color(0xFF667EEA), Color(0xFF764BA2))
    }

    val glowColor = if (isRecording) {
        Color(0xFFEF4444).copy(alpha = 0.4f)
    } else {
        PrimaryColors.glow.copy(alpha = 0.3f)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(88.dp)
            .scale(buttonScale)
            .shadow(20.dp, CircleShape, spotColor = glowColor)
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(buttonColors))
            .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                if (isRecording) onStopClick() else onRecordClick()
            }
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = if (isRecording) "Зупинити" else "Записати",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = when {
            isRecording -> "Натисни щоб зупинити"
            orbState == OrbState.THINKING -> "AI думає..."
            orbState == OrbState.SPEAKING -> "AI говорить..."
            orbState == OrbState.COMPLETE -> "Завершено"
            else -> "Натисни щоб говорити"
        },
        style = AppTypography.labelSmall,
        color = TextColors.onDarkMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun CompletionButtons(
    onAnalyzeClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val view = LocalView.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Primary button — "Отримати аналіз"
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(52.dp)
                .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFF667EEA).copy(alpha = 0.4f))
                .clip(RoundedCornerShape(26.dp))
                .background(brush = Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2))))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(26.dp))
                .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onAnalyzeClick() }
        ) {
            Text(
                text = "Отримати аналіз",
                style = AppTypography.titleSmall,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Secondary button — "Продовжити без аналізу"
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(22.dp))
                .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onSkipClick() }
        ) {
            Text(
                text = "Продовжити без аналізу",
                style = AppTypography.labelMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEF4444).copy(alpha = 0.15f))
            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = message,
            style = AppTypography.bodySmall,
            color = Color(0xFFFCA5A5),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// =====================================================================
// ImprovisationAnalysisScreen — Results display with score reveal
// =====================================================================

@Composable
fun ImprovisationAnalysisScreen(
    result: VoiceAnalysisResult,
    exerciseTitle: String,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    // === REVEAL ORCHESTRATION ===
    var showScore by remember { mutableStateOf(false) }
    var showBars by remember { mutableStateOf(false) }
    var showStrengths by remember { mutableStateOf(false) }
    var showImprovements by remember { mutableStateOf(false) }
    var showTip by remember { mutableStateOf(false) }
    var showCoach by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var showSparkle by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        showScore = true
        kotlinx.coroutines.delay(900)
        showBars = true
        kotlinx.coroutines.delay(1100)
        showStrengths = true
        kotlinx.coroutines.delay(300)
        showImprovements = true
        kotlinx.coroutines.delay(300)
        showTip = true
        kotlinx.coroutines.delay(200)
        showCoach = true
        kotlinx.coroutines.delay(200)
        showButton = true
        if (result.overallScore >= 80) {
            showSparkle = true
        }
    }

    // === SCORE ANIMATIONS ===
    val animatedScore by animateIntAsState(
        targetValue = if (showScore) result.overallScore else 0,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "scoreCountUp"
    )

    val circleScale by animateFloatAsState(
        targetValue = if (showScore) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "circleScale"
    )

    val circleAlpha by animateFloatAsState(
        targetValue = if (showScore) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "circleAlpha"
    )

    val arcProgress by animateFloatAsState(
        targetValue = if (showScore) result.overallScore / 100f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "arcProgress"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (showScore) 0.5f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "glowAlpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // === TOP BAR ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Результати аналiзу",
                    style = AppTypography.titleMedium,
                    color = TextColors.onDarkPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === ANIMATED OVERALL SCORE ===
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer {
                        scaleX = circleScale
                        scaleY = circleScale
                        alpha = circleAlpha
                    },
                contentAlignment = Alignment.Center
            ) {
                // Glow shadow
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = CircleShape,
                            spotColor = Color(0xFF667EEA).copy(alpha = glowAlpha)
                        )
                )

                // Arc progress ring
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.size(136.dp)
                ) {
                    // Background ring
                    drawArc(
                        color = Color.White.copy(alpha = 0.1f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 6.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                    // Filled arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF4ECDC4),
                                Color(0xFF667EEA),
                                Color(0xFF764BA2),
                                Color(0xFF4ECDC4)
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * arcProgress,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 6.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }

                // Score circle background
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667EEA).copy(alpha = 0.3f),
                                    Color(0xFF764BA2).copy(alpha = 0.3f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$animatedScore",
                            style = AppTypography.displayLarge,
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "балiв",
                            style = AppTypography.labelSmall,
                            color = TextColors.onDarkSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Sparkle particles for high scores
                if (showSparkle) {
                    ScoreSparkleEffect()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = showScore,
                enter = fadeIn(tween(300))
            ) {
                Text(
                    text = exerciseTitle,
                    style = AppTypography.titleMedium,
                    color = TextColors.onDarkSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === ANIMATED SCORE BARS ===
            AnimatedVisibility(
                visible = showBars,
                enter = fadeIn(tween(300)) + expandVertically(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    expandFrom = Alignment.Top
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnimatedScoreBar("Дикцiя", result.diction, Color(0xFF60A5FA), showBars, 0)
                    AnimatedScoreBar("Темп", result.tempo, Color(0xFF34D399), showBars, 100)
                    AnimatedScoreBar("Виразнiсть", result.intonation, Color(0xFFFBBF24), showBars, 200)
                    AnimatedScoreBar("Впевненiсть", result.confidence, Color(0xFFA78BFA), showBars, 300)
                    AnimatedScoreBar("Без паразитiв", result.fillerWords, Color(0xFFF472B6), showBars, 400)
                    AnimatedScoreBar("Структура", result.structure, Color(0xFF2DD4BF), showBars, 500)
                    AnimatedScoreBar("Переконливiсть", result.persuasiveness, Color(0xFFFF8C42), showBars, 600)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === STRENGTHS ===
            if (result.strengths.isNotEmpty()) {
                AnimatedVisibility(
                    visible = showStrengths,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Сильнi сторони",
                            style = AppTypography.titleSmall,
                            color = Color(0xFF34D399),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        result.strengths.forEach { strength ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "\u2705", fontSize = 14.sp)
                                Text(
                                    text = strength,
                                    style = AppTypography.bodyMedium,
                                    color = TextColors.onDarkPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // === IMPROVEMENTS ===
            if (result.improvements.isNotEmpty()) {
                AnimatedVisibility(
                    visible = showImprovements,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF59E0B).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Зони покращення",
                            style = AppTypography.titleSmall,
                            color = Color(0xFFFBBF24),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        result.improvements.forEach { improvement ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "\u26A0\uFE0F", fontSize = 14.sp)
                                Text(
                                    text = improvement,
                                    style = AppTypography.bodyMedium,
                                    color = TextColors.onDarkPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // === TIP ===
            if (result.tip.isNotBlank()) {
                AnimatedVisibility(
                    visible = showTip,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF667EEA).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFF667EEA).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "\uD83D\uDCA1 Порада",
                            style = AppTypography.titleSmall,
                            color = Color(0xFF93C5FD),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = result.tip,
                            style = AppTypography.bodyMedium,
                            color = TextColors.onDarkPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // === COACH COMMENT ===
            if (result.coachComment.isNotBlank()) {
                AnimatedVisibility(
                    visible = showCoach,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF764BA2).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFF764BA2).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "\uD83C\uDFAF Коментар тренера",
                            style = AppTypography.titleSmall,
                            color = Color(0xFFC084FC),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = result.coachComment,
                            style = AppTypography.bodyMedium,
                            color = TextColors.onDarkPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === DONE BUTTON ===
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(300)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(52.dp)
                        .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFF667EEA).copy(alpha = 0.4f))
                        .clip(RoundedCornerShape(26.dp))
                        .background(brush = Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2))))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(26.dp))
                        .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onDismiss() }
                ) {
                    Text(
                        text = "Готово",
                        style = AppTypography.titleSmall,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Sparkle particles for high scores (>= 80).
 * Small gradient dots that scale in and drift outward.
 */
@Composable
private fun ScoreSparkleEffect() {
    val particles = remember {
        List(8) { i ->
            val angle = (i * 45f) * (Math.PI / 180f).toFloat()
            Triple(angle, 60f + (i % 3) * 10f, 200L + (i * 80L))
        }
    }

    particles.forEach { (angle, radius, delay) ->
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(delay)
            visible = true
        }

        val scale by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "sparkle_$angle"
        )

        val fadeOut by animateFloatAsState(
            targetValue = if (visible) 0f else 1f,
            animationSpec = tween(1200, delayMillis = 400, easing = LinearEasing),
            label = "sparkleFade_$angle"
        )

        val offsetX = kotlin.math.cos(angle) * radius
        val offsetY = kotlin.math.sin(angle) * radius
        val size = 4.dp + (angle.toInt() % 3).dp

        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size(size)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - fadeOut
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFF667EEA).copy(alpha = 0.6f)
                        )
                    ),
                    CircleShape
                )
        )
    }
}

/**
 * Animated score bar with staggered fill animation.
 */
@Composable
private fun AnimatedScoreBar(
    label: String,
    score: Int,
    color: Color,
    animate: Boolean,
    delayMs: Int
) {
    var startFill by remember { mutableStateOf(false) }

    LaunchedEffect(animate) {
        if (animate) {
            kotlinx.coroutines.delay(delayMs.toLong())
            startFill = true
        }
    }

    val animatedFill by animateFloatAsState(
        targetValue = if (startFill) score / 100f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "barFill_$label"
    )

    val animatedScoreNum by animateIntAsState(
        targetValue = if (startFill) score else 0,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "barScore_$label"
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$animatedScoreNum",
                style = AppTypography.bodyMedium,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedFill)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

// =====================================================================
// AnalyzingScreen — Loading state
// =====================================================================

@Composable
fun AnalyzingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "analyzing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pulsing orb
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(pulseScale)
                    .shadow(
                        24.dp,
                        CircleShape,
                        spotColor = Color(0xFF667EEA).copy(alpha = glowAlpha)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF667EEA),
                                Color(0xFF764BA2)
                            )
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    strokeWidth = 3.dp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Аналiзую ваш виступ...",
                style = AppTypography.titleMedium,
                color = TextColors.onDarkPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Це може зайняти кiлька секунд",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

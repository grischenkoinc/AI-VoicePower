package com.aivoicepower.ui.screens.tonguetwister

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.domain.model.content.TongueTwister
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun TongueTwistersScreen(
    viewModel: TongueTwistersViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 20.dp)
        ) {
            // Header
            TongueTwisterHeader(
                onNavigateBack = onNavigateBack,
                totalCount = state.filteredTongueTwisters.size
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Search Bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onEvent(TongueTwistersEvent.UpdateSearch(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filters
            CategoryFilters(
                selectedCategory = state.selectedCategory,
                onCategorySelect = { viewModel.onEvent(TongueTwistersEvent.SelectCategory(it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Difficulty Filters
            DifficultyFilters(
                selectedDifficulty = state.selectedDifficulty,
                onDifficultySelect = { viewModel.onEvent(TongueTwistersEvent.SelectDifficulty(it)) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tongue Twisters List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.filteredTongueTwisters) { twister ->
                    TongueTwisterCard(
                        twister = twister,
                        isExpanded = state.expandedTwisterId == twister.id,
                        onToggleExpand = { viewModel.onEvent(TongueTwistersEvent.ToggleExpand(twister.id)) },
                        onPractice = { viewModel.onEvent(TongueTwistersEvent.StartPractice(twister)) }
                    )
                }
            }
        }

        // Practice Dialog
        if (state.isPracticing && state.practicingTwister != null) {
            PracticeDialog(
                twister = state.practicingTwister!!,
                isRecording = state.isRecording,
                recordingDurationMs = state.recordingDurationMs,
                isAnalyzing = state.isAnalyzing,
                analysisResult = state.analysisResult,
                onDismiss = { viewModel.onEvent(TongueTwistersEvent.StopPractice) },
                onStartRecording = { viewModel.onEvent(TongueTwistersEvent.StartRecording) },
                onStopRecording = { viewModel.onEvent(TongueTwistersEvent.StopRecording) },
                onDismissAnalysis = { viewModel.onEvent(TongueTwistersEvent.DismissAnalysis) }
            )
        }
    }
}

@Composable
private fun TongueTwisterHeader(
    onNavigateBack: () -> Unit,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "$totalCount скоромовок",
                style = AppTypography.labelMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "👅 Скоромовки",
                style = AppTypography.displayLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp
            )
        }

        Row(
            modifier = Modifier
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .background(Color.White, RoundedCornerShape(16.dp))
                .clickable { onNavigateBack() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 24.sp,
                color = Color(0xFF667EEA),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Назад",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "🔍 Шукати скоромовки...",
                color = TextColors.onLightMuted,
                fontSize = 15.sp
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            ),
        singleLine = true
    )
}

@Composable
private fun CategoryFilters(
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Всі" to null,
        "Р" to "Р",
        "Л" to "Л",
        "С-З-Ц" to "С-З-Ц",
        "Ш-Ж-Ч-Щ" to "Ш-Ж-Ч-Щ",
        "Комбіновані" to "Комбіновані"
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (label, category) ->
            FilterChip(
                label = label,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelect(category) }
            )
        }
    }
}

@Composable
private fun DifficultyFilters(
    selectedDifficulty: Int?,
    onDifficultySelect: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            label = "Всі",
            isSelected = selectedDifficulty == null,
            onClick = { onDifficultySelect(null) }
        )

        (1..5).forEach { difficulty ->
            FilterChip(
                label = "⭐".repeat(difficulty),
                isSelected = selectedDifficulty == difficulty,
                onClick = { onDifficultySelect(difficulty) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = if (isSelected) 12.dp else 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (isSelected) Color(0xFF667EEA).copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.1f)
            )
            .background(
                if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(Color.White, Color.White)
                    )
                },
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = if (isSelected) Color.White else TextColors.onLightPrimary,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
private fun TongueTwisterCard(
    twister: TongueTwister,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable { onToggleExpand() }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header with difficulty
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Difficulty stars
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(twister.difficulty) {
                    Text(text = "⭐", fontSize = 14.sp)
                }
            }

            // Category badge
            twister.category?.let { category ->
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0x40667EEA), Color(0x40764BA2))
                            ),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = category,
                        style = AppTypography.labelSmall,
                        color = Color(0xFF667EEA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Tongue twister text
        Text(
            text = twister.text,
            style = AppTypography.bodyLarge,
            color = TextColors.onLightPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp
        )

        // Expanded content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Divider(
                    color = Color(0xFFE5E7EB),
                    thickness = 1.dp
                )

                // Target sounds
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯 Звуки:",
                        style = AppTypography.labelMedium,
                        color = TextColors.onLightSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    twister.targetSounds.forEach { sound ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFF3F4F6),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = sound,
                                style = AppTypography.labelSmall,
                                color = TextColors.onLightPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Practice button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color(0xFF10B981).copy(alpha = 0.3f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF10B981), Color(0xFF059669))
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onPractice() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎤 Практикувати",
                        style = AppTypography.bodyLarge,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PracticeDialog(
    twister: TongueTwister,
    isRecording: Boolean,
    recordingDurationMs: Long,
    isAnalyzing: Boolean,
    analysisResult: VoiceAnalysisResult?,
    onDismiss: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onDismissAnalysis: () -> Unit
) {
    // Pulsing border animation for recording state
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .then(
                    if (isRecording) {
                        Modifier.border(
                            width = 4.dp,
                            color = Color(0xFF3B82F6).copy(alpha = borderAlpha),
                            shape = RoundedCornerShape(28.dp)
                        )
                    } else Modifier
                )
                .shadow(
                    elevation = 32.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = if (isRecording) Color(0xFF667EEA).copy(alpha = 0.4f)
                    else Color.Black.copy(alpha = 0.3f)
                )
                .background(Color.White, RoundedCornerShape(28.dp))
                .verticalScroll(rememberScrollState())
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (analysisResult != null) "Результат" else "Практика",
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                if (!isAnalyzing) {
                    Text(
                        text = "✕",
                        fontSize = 28.sp,
                        color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { if (analysisResult != null) onDismissAnalysis() else onDismiss() }
                            .padding(4.dp)
                    )
                }
            }

            if (analysisResult != null) {
                // === ANALYSIS RESULTS ===
                AnalysisResultsContent(result = analysisResult, onDismiss = onDismissAnalysis)
            } else if (isAnalyzing) {
                // === ANALYZING STATE ===
                AnalyzingContent()
            } else {
                // === RECORDING / IDLE STATE ===

                // Difficulty
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(twister.difficulty) {
                        Text(text = "⭐", fontSize = 16.sp)
                    }
                }

                // Tongue twister text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFF9FAFB), Color(0xFFF3F4F6))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = twister.text,
                        style = AppTypography.bodyLarge,
                        color = TextColors.onLightPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Microphone button with wave rings
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Wave rings when recording
                    if (isRecording) {
                        repeat(3) { index ->
                            WaveRing(
                                delay = index * 600,
                                color = Color(0xFF667EEA)
                            )
                        }
                    }

                    // Main microphone button
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                ),
                                CircleShape
                            )
                            .clickable(onClick = if (isRecording) onStopRecording else onStartRecording),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isRecording) "⏹" else "🎤",
                            fontSize = 48.sp
                        )
                    }
                }

                // Timer and status text
                if (isRecording) {
                    val seconds = (recordingDurationMs / 1000).toInt()
                    val minutes = seconds / 60
                    val secs = seconds % 60

                    Text(
                        text = String.format("%d:%02d", minutes, secs),
                        style = AppTypography.displayLarge,
                        color = Color(0xFF667EEA),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = if (isRecording) "Йде запис..." else "Натисни для запису",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun WaveRing(
    delay: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_$delay")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale_$delay"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha_$delay"
    )

    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(
                color = color.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

@Composable
private fun AnalyzingContent() {
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(vertical = 24.dp)
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
private fun AnalysisResultsContent(
    result: VoiceAnalysisResult,
    onDismiss: () -> Unit
) {
    // Overall score with color
    val scoreColor = when (result.overallScore) {
        in 0..39 -> Color(0xFFEF4444)
        in 40..69 -> Color(0xFFF59E0B)
        else -> Color(0xFF22C55E)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall score circle
        Box(
            modifier = Modifier
                .size(100.dp)
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
                    text = "${result.overallScore}",
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

        // Metrics
        val metrics = listOf(
            "Дикція" to result.diction,
            "Темп" to result.tempo,
            "Плавність" to result.intonation,
            "Гучність" to result.volume
        )

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

        // Strengths
        if (result.strengths.isNotEmpty()) {
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

        // Improvements
        if (result.improvements.isNotEmpty()) {
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

        // Tip
        if (result.tip.isNotEmpty()) {
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

        // Done button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(14.dp),
                    spotColor = Color(0xFF667EEA).copy(alpha = 0.3f)
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ),
                    RoundedCornerShape(14.dp)
                )
                .clickable { onDismiss() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Готово",
                style = AppTypography.bodyLarge,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AnalysisMetricBar(
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

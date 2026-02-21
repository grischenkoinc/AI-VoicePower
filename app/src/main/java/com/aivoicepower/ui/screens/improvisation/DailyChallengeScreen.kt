package com.aivoicepower.ui.screens.improvisation

import android.app.Activity
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.ui.components.FocusCountdownOverlay
import com.aivoicepower.ui.components.AnalysisLimitBottomSheet
import com.aivoicepower.ui.components.AnalysisLimitInfo
import com.aivoicepower.ui.components.AnalysisResultsContent
import com.aivoicepower.ui.components.AnalyzingContent
import com.aivoicepower.ui.screens.improvisation.components.DailyChallengeCard
import com.aivoicepower.ui.screens.improvisation.components.ImprovisationRecordingCard
import com.aivoicepower.ui.screens.improvisation.components.PreparationTimerCard
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.components.PrimaryButton
import com.aivoicepower.utils.constants.FreeTierLimits
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@Composable
fun DailyChallengeScreen(
    viewModel: DailyChallengeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit,
    rewardedAdManager: RewardedAdManager? = null,
    onNavigateToPremium: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var backPressedTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current
    val activity = context as? Activity

    // Analysis Limit Bottom Sheet
    if (state.showAnalysisLimitSheet) {
        AnalysisLimitBottomSheet(
            limitInfo = AnalysisLimitInfo(
                usedAnalyses = FreeTierLimits.FREE_IMPROV_ANALYSES_PER_DAY,
                maxFreeAnalyses = FreeTierLimits.FREE_IMPROV_ANALYSES_PER_DAY,
                remainingAdAnalyses = state.remainingAdImprovAnalyses,
                isAdLoaded = state.isAdLoaded,
                isImprovisation = true
            ),
            onWatchAd = {
                viewModel.onEvent(DailyChallengeEvent.WatchAdForAnalysis)
                if (activity != null && rewardedAdManager != null) {
                    rewardedAdManager.showAd(
                        activity = activity,
                        onRewarded = { viewModel.proceedWithAnalysisAfterAd() },
                        onFailed = { error ->
                            scope.launch { snackbarHostState.showSnackbar(error) }
                        }
                    )
                }
            },
            onPremium = {
                viewModel.onEvent(DailyChallengeEvent.DismissAnalysisLimitSheet)
                onNavigateToPremium()
            },
            onContinueWithout = {
                viewModel.onEvent(DailyChallengeEvent.ContinueWithoutAnalysis)
            },
            onDismiss = {
                viewModel.onEvent(DailyChallengeEvent.DismissAnalysisLimitSheet)
            }
        )
    }

    // Double-back to exit protection
    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            onNavigateBack()
        } else {
            backPressedTime = currentTime
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Для виходу натисніть Назад ще раз",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // Auto-start preparation timer when challenge is loaded
    LaunchedEffect(state.challenge, state.isPreparationPhase) {
        if (state.challenge != null && state.isPreparationPhase &&
            state.preparationTimeLeft == 15 && !state.isCompleted) {
            viewModel.onEvent(DailyChallengeEvent.StartPreparation)
        }
    }

    var showFocus by remember { mutableStateOf(false) }
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Імпровізація",
                        style = AppTypography.labelMedium,
                        color = TextColors.onDarkSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "🏆 Щоденний челендж",
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
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        )
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onNavigateBack() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u2190",
                        fontSize = 24.sp,
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "\u041D\u0430\u0437\u0430\u0434",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TextColors.onDarkPrimary)
                    }
                }

                state.isCompleted -> {
                    // Already completed today
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.challenge?.let { challenge ->
                            DailyChallengeCard(
                                challenge = challenge,
                                isCompleted = true
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
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "✓ Виклик виконано!",
                                    style = AppTypography.headlineSmall,
                                    color = Color(0xFF10B981),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Тривалість: ${formatDuration(state.recordingDurationMs)}",
                                    style = AppTypography.bodyLarge,
                                    color = TextColors.onLightPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Повертайся завтра за новим викликом!",
                                    style = AppTypography.bodyMedium,
                                    color = TextColors.onLightSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            val today = LocalDate.now()
                            val tomorrow = today.plusDays(1)
                            val formatter = DateTimeFormatter.ofPattern("dd MMMM", java.util.Locale("uk", "UA"))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 12.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        spotColor = Color.Black.copy(alpha = 0.12f)
                                    )
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Наступний виклик:",
                                    style = AppTypography.bodyMedium,
                                    color = TextColors.onLightSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = tomorrow.format(formatter),
                                    style = AppTypography.titleMedium,
                                    color = TextColors.onLightPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.challenge?.let { challenge ->
                            when {
                                state.isAnalyzing -> {
                                    AnalyzingContent()
                                }

                                state.analysisResult != null -> {
                                    AnalysisResultsContent(
                                        result = state.analysisResult!!,
                                        onDismiss = {
                                            viewModel.onEvent(DailyChallengeEvent.DismissAnalysis)
                                            onNavigateBack()
                                        },
                                        dismissButtonText = "Готово"
                                    )
                                }

                                state.isPreparationPhase -> {
                                    // Preparation phase
                                    DailyChallengeCard(
                                        challenge = challenge,
                                        isCompleted = false
                                    )

                                    PreparationTimerCard(
                                        timeLeft = state.preparationTimeLeft,
                                        onGenerateNew = null // Can't regenerate daily challenge
                                    )

                                    if (state.preparationTimeLeft > 0) {
                                        PrimaryButton(
                                            text = "Почати вправу",
                                            onClick = {
                                                showFocus = true
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                state.isRecording -> {
                                    // Recording phase
                                    ImprovisationRecordingCard(
                                        title = challenge.title,
                                        subtitle = challenge.description,
                                        durationMs = state.recordingDurationMs,
                                        onStop = {
                                            viewModel.onEvent(DailyChallengeEvent.StopRecording)
                                        }
                                    )
                                }

                                else -> {
                                    // Recording completed but not yet saved
                                    DailyChallengeCard(
                                        challenge = challenge,
                                        isCompleted = false
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
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "✓ Запис завершено",
                                            style = AppTypography.titleLarge,
                                            color = TextColors.onLightPrimary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Тривалість: ${formatDuration(state.recordingDurationMs)}",
                                            style = AppTypography.bodyMedium,
                                            color = TextColors.onLightSecondary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    PrimaryButton(
                                        text = "Отримати аналіз",
                                        onClick = {
                                            viewModel.onEvent(DailyChallengeEvent.CompleteChallenge)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        // Error message
                        state.error?.let { error ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 12.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        spotColor = Color(0xFFEF4444).copy(alpha = 0.2f)
                                    )
                                    .background(Color(0xFFFEF2F2), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = error,
                                    style = AppTypography.bodyMedium,
                                    color = Color(0xFFDC2626),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF667EEA),
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Focus countdown overlay
        if (showFocus) {
            FocusCountdownOverlay(
                exerciseName = "Щоденний виклик",
                topic = state.challenge?.title ?: "",
                onComplete = {
                    showFocus = false
                    viewModel.onEvent(DailyChallengeEvent.SkipPreparation)
                }
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

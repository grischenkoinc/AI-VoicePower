package com.aivoicepower.ui.screens.improvisation

import android.app.Activity
import android.view.HapticFeedbackConstants
import android.widget.Toast
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
import com.aivoicepower.domain.model.exercise.StoryFormat
import com.aivoicepower.ui.components.FocusCountdownOverlay
import com.aivoicepower.ui.components.AnalysisLimitBottomSheet
import com.aivoicepower.ui.components.AnalysisLimitInfo
import com.aivoicepower.ui.components.AnalysisResultsContent
import com.aivoicepower.ui.components.AnalyzingContent
import com.aivoicepower.ui.screens.improvisation.components.ImprovisationRecordingCard
import com.aivoicepower.ui.screens.improvisation.components.PreparationTimerCard
import com.aivoicepower.ui.screens.improvisation.components.StoryFormatCard
import com.aivoicepower.ui.screens.improvisation.components.StoryPromptCard
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.components.PrimaryButton
import com.aivoicepower.ui.theme.components.SecondaryButton
import com.aivoicepower.utils.constants.FreeTierLimits

@Composable
fun StorytellingScreen(
    viewModel: StorytellingViewModel = hiltViewModel(),
    rewardedAdManager: RewardedAdManager? = null,
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit,
    onNavigateToPremium: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity
    var backPressedTime by remember { mutableStateOf(0L) }

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
                viewModel.onEvent(StorytellingEvent.WatchAdForAnalysis)
                if (activity != null && rewardedAdManager != null) {
                    rewardedAdManager.showAd(
                        activity = activity,
                        onRewarded = { viewModel.proceedWithAnalysisAfterAd() },
                        onFailed = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onPremium = {
                viewModel.onEvent(StorytellingEvent.DismissAnalysisLimitSheet)
                onNavigateToPremium()
            },
            onContinueWithout = {
                viewModel.onEvent(StorytellingEvent.ContinueWithoutAnalysis)
            },
            onDismiss = {
                viewModel.onEvent(StorytellingEvent.DismissAnalysisLimitSheet)
            }
        )
    }

    // Double-back to exit protection (only when NOT on format selection)
    BackHandler(enabled = state.selectedFormat != null) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            onNavigateBack()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, "Натисніть ще раз, щоб повернутись", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-start preparation timer when format is selected
    LaunchedEffect(state.selectedFormat, state.isPreparationPhase) {
        if (state.selectedFormat != null && state.isPreparationPhase && state.preparationTimeLeft == 15) {
            viewModel.onEvent(StorytellingEvent.StartPreparation)
        }
    }

    var showFocus by remember { mutableStateOf(false) }
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        text = "📖 Розкажи історію",
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

            when {
                state.selectedFormat == null -> {
                    // Format selection
                    Text(
                        text = "Обери формат історії:",
                        style = AppTypography.titleLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    StoryFormat.values().forEach { format ->
                        StoryFormatCard(
                            format = format,
                            onClick = {
                                viewModel.onEvent(StorytellingEvent.SelectFormat(format))
                            }
                        )
                    }
                }

                state.isAnalyzing -> {
                    AnalyzingContent()
                }

                state.analysisResult != null -> {
                    AnalysisResultsContent(
                        result = state.analysisResult!!,
                        onDismiss = {
                            viewModel.onEvent(StorytellingEvent.DismissAnalysis)
                            onNavigateBack()
                        },
                        dismissButtonText = "Готово"
                    )
                }

                state.isPreparationPhase -> {
                    // Preparation phase
                    StoryPromptCard(
                        format = state.selectedFormat!!,
                        prompt = state.storyPrompt
                    )

                    PreparationTimerCard(
                        timeLeft = state.preparationTimeLeft,
                        onGenerateNew = {
                            viewModel.onEvent(StorytellingEvent.GenerateNewPrompt)
                        }
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

                    SecondaryButton(
                        text = "← Змінити формат",
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.ResetToFormatSelection)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                state.isRecording -> {
                    ImprovisationRecordingCard(
                        title = "Розповідай історію",
                        subtitle = state.storyPrompt,
                        durationMs = state.recordingDurationMs,
                        onStop = {
                            viewModel.onEvent(StorytellingEvent.StopRecording)
                        }
                    )
                }

                else -> {
                    // Recording completed
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
                            text = "✓ Історія записана",
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
                            viewModel.onEvent(StorytellingEvent.CompleteTask)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    SecondaryButton(
                        text = "🔄 Нова історія",
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.GenerateNewPrompt)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    SecondaryButton(
                        text = "← Змінити формат",
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.ResetToFormatSelection)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
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

        // Focus countdown overlay
        if (showFocus) {
            FocusCountdownOverlay(
                exerciseName = "Сторiтелiнг",
                topic = state.storyPrompt,
                onComplete = {
                    showFocus = false
                    viewModel.onEvent(StorytellingEvent.SkipPreparation)
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

package com.aivoicepower.ui.screens.courses.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.data.ads.BannerAdView
import com.aivoicepower.ui.components.AnalysisResultsContent
import com.aivoicepower.ui.components.AnalyzingContent
import com.aivoicepower.ui.components.NoAnalysisResultCard
import com.aivoicepower.ui.screens.courses.ExerciseState
import com.aivoicepower.ui.screens.courses.ExerciseStatus
import com.aivoicepower.ui.screens.courses.LessonEvent
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*

@Composable
fun ExercisePhaseContent(
    lesson: Lesson,
    currentExerciseIndex: Int,
    exerciseState: ExerciseState?,
    exerciseStates: List<ExerciseState> = emptyList(),
    totalExercises: Int,
    isPlaying: Boolean,
    onEvent: (LessonEvent) -> Unit,
    modifier: Modifier = Modifier,
    remainingAnalyses: Int? = null,
    onContinueAfterNoAnalysis: (() -> Unit)? = null
) {
    if (exerciseState == null) return

    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground(
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Fixed Progress Header (stays in place)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        ProgressBar3D(
                            progress = (currentExerciseIndex + 1) / totalExercises.toFloat(),
                            currentStep = currentExerciseIndex + 1,
                            totalSteps = totalExercises,
                            stepLabel = "Вправа"
                        )
                    }

                    // Animated exercise content
                    AnimatedContent(
                        targetState = currentExerciseIndex,
                        transitionSpec = {
                            // Use initialState (reliable) instead of manual previousIndex tracking
                            if (targetState > initialState) {
                                // Forward: new enters from RIGHT, old exits LEFT
                                // Exactly matching the theory→exercise phase transition
                                (fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 3 })
                                    .togetherWith(
                                        fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 3 })
                            } else {
                                // Back: new enters from LEFT, old exits RIGHT
                                (fadeIn(tween(400)) + slideInHorizontally(tween(400)) { -it / 3 })
                                    .togetherWith(
                                        fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 3 })
                            }
                        },
                        label = "exerciseTransition"
                    ) { animatedIndex ->
                        val animatedExerciseState = exerciseStates.getOrNull(animatedIndex) ?: return@AnimatedContent
                        val scrollState = rememberScrollState()

                        // Reset scroll when exercise changes
                        LaunchedEffect(animatedIndex) {
                            scrollState.scrollTo(0)
                        }

                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Пульсуюча рамка навколо всієї картки при записі
                        val isRecording = animatedExerciseState.status == ExerciseStatus.Recording

                        val infiniteTransition = rememberInfiniteTransition(label = "border")
                        val borderAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "alpha"
                        )

                        // Wrapper з пульсуючою рамкою
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (isRecording) {
                                        Modifier.border(
                                            width = 4.dp,
                                            color = Color(0xFF3B82F6).copy(alpha = borderAlpha),
                                            shape = RoundedCornerShape(32.dp)
                                        )
                                    } else Modifier
                                )
                        ) {
                            // Exercise Card з premium дизайном
                            PracticeCard(
                                header = {
                                SectionTag(
                                    emoji = getExerciseEmoji(animatedExerciseState.exercise.type),
                                    text = "${animatedIndex + 1}/$totalExercises",
                                    isPractice = true
                                )

                                // BigTitle inline
                                Text(
                                    text = animatedExerciseState.exercise.title,
                                    style = AppTypography.displayLarge,
                                    color = TextColors.onDarkPrimary,
                                    fontSize = 36.sp,
                                    lineHeight = 40.sp,
                                    letterSpacing = (-1.5).sp
                                )
                            },
                            content = {
                                // Remaining analyses indicator for free users
                                if (remainingAnalyses != null && animatedExerciseState.status != ExerciseStatus.Analyzing &&
                                    animatedExerciseState.status != ExerciseStatus.ShowingResults &&
                                    animatedExerciseState.status != ExerciseStatus.CompletedWithoutAnalysis &&
                                    animatedExerciseState.exercise.type != com.aivoicepower.domain.model.exercise.ExerciseType.ARTICULATION &&
                                    animatedExerciseState.exercise.type != com.aivoicepower.domain.model.exercise.ExerciseType.BREATHING
                                ) {
                                    val color = when {
                                        remainingAnalyses > 3 -> Color(0xFF22C55E) // green
                                        remainingAnalyses > 1 -> Color(0xFFF59E0B) // orange
                                        else -> Color(0xFFEF4444) // red
                                    }
                                    Text(
                                        text = "\uD83D\uDD2C Аналізів залишилось: $remainingAnalyses",
                                        color = color,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                when (animatedExerciseState.status) {
                                    ExerciseStatus.Analyzing -> {
                                        AnalyzingContent()
                                    }
                                    ExerciseStatus.ShowingResults -> {
                                        animatedExerciseState.analysisResult?.let { result ->
                                            AnalysisResultsContent(
                                                result = result,
                                                onDismiss = { onEvent(LessonEvent.ContinueAfterAnalysisClicked) },
                                                dismissButtonText = "Далі",
                                                onRetry = { onEvent(LessonEvent.RetryExerciseClicked) }
                                            )
                                        }
                                        // Banner ad for free users after analysis
                                        if (remainingAnalyses != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            BannerAdView()
                                        }
                                    }
                                    ExerciseStatus.CompletedWithoutAnalysis -> {
                                        NoAnalysisResultCard(
                                            recordingDurationMs = animatedExerciseState.recordingDurationMs
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        PrimaryButton(
                                            text = "Далі",
                                            onClick = { onContinueAfterNoAnalysis?.invoke() },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    else -> {
                                        // Exercise Card (окремий компонент)
                                        ExerciseCard(exerciseState = animatedExerciseState)

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Recording Controls (не показувати для ARTICULATION та BREATHING)
                                        if (animatedExerciseState.exercise.type != com.aivoicepower.domain.model.exercise.ExerciseType.ARTICULATION &&
                                            animatedExerciseState.exercise.type != com.aivoicepower.domain.model.exercise.ExerciseType.BREATHING) {
                                            RecordingControls(
                                                exerciseState = animatedExerciseState,
                                                isPlaying = isPlaying,
                                                onStartRecording = { onEvent(LessonEvent.StartRecordingClicked) },
                                                onStopRecording = { onEvent(LessonEvent.StopRecordingClicked) },
                                                onPlayRecording = { onEvent(LessonEvent.PlayRecordingClicked) },
                                                onStopPlayback = { onEvent(LessonEvent.StopPlaybackClicked) },
                                                onReRecord = { onEvent(LessonEvent.ReRecordClicked) },
                                                onComplete = { onEvent(LessonEvent.CompleteExerciseClicked) }
                                            )
                                        }
                                    }
                                }
                            }
                        )
                        }

                        // Navigation
                        BottomNavRow(
                            onPrevious = if (animatedIndex == 0) {
                                { onEvent(LessonEvent.NavigateBackClicked) }
                            } else {
                                { onEvent(LessonEvent.PreviousExerciseClicked) }
                            },
                            onNext = { onEvent(LessonEvent.SkipExerciseClicked) },
                            previousText = if (animatedIndex == 0) "До теорії" else "Назад",
                            nextText = "Далі"
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    } // AnimatedContent
                }
            }
        )
    }
}

// Helper для емодзі типів вправ
private fun getExerciseEmoji(type: com.aivoicepower.domain.model.exercise.ExerciseType): String {
    return when (type) {
        com.aivoicepower.domain.model.exercise.ExerciseType.ARTICULATION -> "🗣️"
        com.aivoicepower.domain.model.exercise.ExerciseType.TONGUE_TWISTER -> "🔥"
        com.aivoicepower.domain.model.exercise.ExerciseType.READING -> "📖"
        com.aivoicepower.domain.model.exercise.ExerciseType.EMOTION_READING -> "🎭"
        com.aivoicepower.domain.model.exercise.ExerciseType.FREE_SPEECH -> "💬"
        com.aivoicepower.domain.model.exercise.ExerciseType.RETELLING -> "📝"
        com.aivoicepower.domain.model.exercise.ExerciseType.DIALOGUE -> "💭"
        com.aivoicepower.domain.model.exercise.ExerciseType.PITCH -> "🎵"
        com.aivoicepower.domain.model.exercise.ExerciseType.QA -> "❓"
        com.aivoicepower.domain.model.exercise.ExerciseType.TONGUE_TWISTER_BATTLE -> "⚔️"
        com.aivoicepower.domain.model.exercise.ExerciseType.MINIMAL_PAIRS -> "👂"
        com.aivoicepower.domain.model.exercise.ExerciseType.CONTRAST_SOUNDS -> "🔊"
        com.aivoicepower.domain.model.exercise.ExerciseType.SLOW_MOTION -> "🐌"
        com.aivoicepower.domain.model.exercise.ExerciseType.BREATHING -> "🌬️"
    }
}

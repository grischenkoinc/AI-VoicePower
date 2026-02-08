package com.aivoicepower.ui.screens.courses.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.components.AnalysisResultsContent
import com.aivoicepower.ui.components.AnalyzingContent
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
    totalExercises: Int,
    isPlaying: Boolean,
    onEvent: (LessonEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (exerciseState == null) return

    val scrollState = rememberScrollState()

    // Reset scroll to top when exercise changes
    LaunchedEffect(currentExerciseIndex) {
        scrollState.animateScrollTo(0)
    }

    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground(
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Fixed Progress Header
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
                        val isRecording = exerciseState.status == ExerciseStatus.Recording

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
                                    emoji = getExerciseEmoji(exerciseState.exercise.type),
                                    text = "${currentExerciseIndex + 1}/$totalExercises",
                                    isPractice = true
                                )

                                // BigTitle inline
                                Text(
                                    text = exerciseState.exercise.title,
                                    style = AppTypography.displayLarge,
                                    color = TextColors.onDarkPrimary,
                                    fontSize = 36.sp,
                                    lineHeight = 40.sp,
                                    letterSpacing = (-1.5).sp
                                )
                            },
                            content = {
                                when (exerciseState.status) {
                                    ExerciseStatus.Analyzing -> {
                                        AnalyzingContent()
                                    }
                                    ExerciseStatus.ShowingResults -> {
                                        exerciseState.analysisResult?.let { result ->
                                            AnalysisResultsContent(
                                                result = result,
                                                onDismiss = { onEvent(LessonEvent.ContinueAfterAnalysisClicked) },
                                                dismissButtonText = "Далі"
                                            )
                                        }
                                    }
                                    else -> {
                                        // Exercise Card (окремий компонент)
                                        ExerciseCard(exerciseState = exerciseState)

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Recording Controls (не показувати для ARTICULATION та BREATHING)
                                        if (exerciseState.exercise.type != com.aivoicepower.domain.model.exercise.ExerciseType.ARTICULATION &&
                                            exerciseState.exercise.type != com.aivoicepower.domain.model.exercise.ExerciseType.BREATHING) {
                                            RecordingControls(
                                                exerciseState = exerciseState,
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
                            onPrevious = if (currentExerciseIndex == 0) {
                                { onEvent(LessonEvent.NavigateBackClicked) }
                            } else {
                                { onEvent(LessonEvent.PreviousExerciseClicked) }
                            },
                            onNext = { onEvent(LessonEvent.SkipExerciseClicked) },
                            previousText = if (currentExerciseIndex == 0) "До теорії" else "Назад",
                            nextText = "Далі"
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
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

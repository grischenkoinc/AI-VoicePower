package com.aivoicepower.ui.screens.courses

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.courses.components.CompletedPhaseContent
import com.aivoicepower.ui.screens.courses.components.ExercisePhaseContent
import com.aivoicepower.ui.screens.courses.components.TheoryPhaseContent
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.components.GradientBackground
import kotlinx.coroutines.launch

@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    viewModel: LessonViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToNextLesson: ((courseId: String, lessonId: String) -> Unit)? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var hasAudioPermission by remember { mutableStateOf(false) }
    var backPressedTime by remember { mutableStateOf(0L) }

    // Double-back to exit protection
    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            // Actually go back
            onNavigateBack()
        } else {
            // Show toast
            backPressedTime = currentTime
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Для виходу натисніть Назад ще раз",
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    // Request permission ONLY when entering Exercise phase
    LaunchedEffect(state.currentPhase) {
        if (state.currentPhase == LessonPhase.Exercise && !hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Show toast message
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = androidx.compose.material3.SnackbarDuration.Long
                )
                viewModel.clearToastMessage()
            }
        }
    }

    when {
        state.isLoading -> {
            GradientBackground(
                content = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            )
        }

        state.error != null -> {
            GradientBackground(
                content = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error!!,
                            color = Color.White,
                            style = AppTypography.bodyLarge
                        )
                    }
                }
            )
        }

        state.lesson != null -> {
            Box(modifier = Modifier.fillMaxSize()) {
                when (state.currentPhase) {
                    LessonPhase.Theory -> {
                        TheoryPhaseContent(
                            lesson = state.lesson!!,
                            onStartExercises = {
                                viewModel.onEvent(LessonEvent.StartExercisesClicked)
                            },
                            onNavigateBack = onNavigateBack
                        )
                    }

                    LessonPhase.Exercise -> {
                        ExercisePhaseContent(
                            lesson = state.lesson!!,
                            currentExerciseIndex = state.currentExerciseIndex,
                            exerciseState = state.exerciseStates.getOrNull(state.currentExerciseIndex),
                            totalExercises = state.exerciseStates.size,
                            isPlaying = state.isPlaying,
                            onEvent = viewModel::onEvent
                        )
                    }

                    LessonPhase.Completed -> {
                        CompletedPhaseContent(
                            lesson = state.lesson!!,
                            nextLesson = state.nextLesson,
                            onFinish = {
                                viewModel.onEvent(LessonEvent.FinishLessonClicked)
                                onNavigateBack()
                            },
                            onNextLesson = state.nextLesson?.let { nextLesson ->
                                {
                                    viewModel.onEvent(LessonEvent.NextLessonClicked)
                                    onNavigateToNextLesson?.invoke(courseId, nextLesson.id)
                                }
                            }
                        )
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
            }
        }
    }
}

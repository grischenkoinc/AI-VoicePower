package com.aivoicepower.ui.screens.courses

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.courses.components.CompletedPhaseContent
import com.aivoicepower.ui.screens.courses.components.ExercisePhaseContent
import com.aivoicepower.ui.screens.courses.components.TheoryPhaseContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    viewModel: LessonViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var hasAudioPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.lesson?.let { "День ${it.dayNumber}: ${it.title}" } ?: "Урок"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            state.lesson != null -> {
                when (state.currentPhase) {
                    LessonPhase.Theory -> {
                        TheoryPhaseContent(
                            lesson = state.lesson!!,
                            onStartExercises = {
                                viewModel.onEvent(LessonEvent.StartExercisesClicked)
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    LessonPhase.Exercise -> {
                        ExercisePhaseContent(
                            lesson = state.lesson!!,
                            currentExerciseIndex = state.currentExerciseIndex,
                            exerciseState = state.exerciseStates.getOrNull(state.currentExerciseIndex),
                            totalExercises = state.exerciseStates.size,
                            isPlaying = state.isPlaying,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    LessonPhase.Completed -> {
                        CompletedPhaseContent(
                            lesson = state.lesson!!,
                            onFinish = {
                                viewModel.onEvent(LessonEvent.FinishLessonClicked)
                                onNavigateBack()
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}

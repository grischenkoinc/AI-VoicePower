package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.warmup.components.VoiceExerciseDialog
import com.aivoicepower.ui.screens.warmup.components.VoiceExerciseItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceWarmupScreen(
    viewModel: VoiceWarmupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Розминка голосу") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Виконано: ${state.completedToday.size}/${state.exercises.size}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    LinearProgressIndicator(
                        progress = state.completedToday.size.toFloat() / state.exercises.size,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }
            }

            // Exercise list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.exercises) { _, exercise ->
                    VoiceExerciseItem(
                        exercise = exercise,
                        isCompleted = state.completedToday.contains(exercise.id),
                        onClick = {
                            viewModel.onEvent(VoiceWarmupEvent.ExerciseClicked(exercise))
                        }
                    )
                }

                // Finish button
                item {
                    if (state.completedToday.size == state.exercises.size) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onNavigateBack()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Завершити розминку ✓")
                        }
                    }
                }
            }
        }

        // Exercise dialog
        if (state.isExerciseDialogOpen && state.selectedExercise != null) {
            VoiceExerciseDialog(
                exercise = state.selectedExercise!!,
                timerSeconds = state.timerSeconds,
                isTimerRunning = state.isTimerRunning,
                isAudioPlaying = state.isAudioPlaying,
                onDismiss = {
                    viewModel.onEvent(VoiceWarmupEvent.ExerciseDialogDismissed)
                },
                onStartTimer = {
                    viewModel.onEvent(VoiceWarmupEvent.StartTimer)
                },
                onPauseTimer = {
                    viewModel.onEvent(VoiceWarmupEvent.PauseTimer)
                },
                onMarkCompleted = {
                    viewModel.onEvent(VoiceWarmupEvent.MarkAsCompleted)
                },
                onSkip = {
                    viewModel.onEvent(VoiceWarmupEvent.SkipExercise)
                },
                onPlayAudio = {
                    viewModel.onEvent(VoiceWarmupEvent.PlayAudioExample)
                },
                onStopAudio = {
                    viewModel.onEvent(VoiceWarmupEvent.StopAudioExample)
                }
            )
        }
    }
}

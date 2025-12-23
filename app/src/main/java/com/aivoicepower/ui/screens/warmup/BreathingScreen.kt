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
import com.aivoicepower.ui.screens.warmup.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    viewModel: BreathingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дихальні вправи") },
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
                    Text(
                        text = "Виконано: ${state.completedToday.size}/${state.exercises.size}",
                        style = MaterialTheme.typography.titleMedium
                    )

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
                    BreathingExerciseItem(
                        exercise = exercise,
                        isCompleted = state.completedToday.contains(exercise.id),
                        onClick = {
                            viewModel.onEvent(BreathingEvent.ExerciseClicked(exercise))
                        }
                    )
                }

                // Finish button
                item {
                    if (state.completedToday.size == state.exercises.size) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Завершити розминку ✓")
                        }
                    }
                }
            }
        }

        // Exercise dialog (fullscreen)
        if (state.isExerciseDialogOpen && state.selectedExercise != null) {
            BreathingExerciseDialog(
                exercise = state.selectedExercise!!,
                elapsedSeconds = state.elapsedSeconds,
                totalSeconds = state.totalSeconds,
                currentPhase = state.currentPhase,
                phaseProgress = state.phaseProgress,
                isRunning = state.isRunning,
                onDismiss = {
                    viewModel.onEvent(BreathingEvent.ExerciseDialogDismissed)
                },
                onStart = {
                    viewModel.onEvent(BreathingEvent.StartBreathing)
                },
                onPause = {
                    viewModel.onEvent(BreathingEvent.PauseBreathing)
                },
                onMarkCompleted = {
                    viewModel.onEvent(BreathingEvent.MarkAsCompleted)
                },
                onSkip = {
                    viewModel.onEvent(BreathingEvent.SkipExercise)
                }
            )
        }
    }
}

package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.warmup.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickWarmupScreen(
    viewModel: QuickWarmupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        // Auto-start first exercise
        viewModel.onEvent(QuickWarmupEvent.StartQuickWarmup)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Швидка розминка (5 хв)") },
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
                        text = "Вправа ${state.currentExerciseIndex + 1} з ${state.exercises.size}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    LinearProgressIndicator(
                        progress = state.completedExercises.size.toFloat() / state.exercises.size,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }
            }

            // Exercise list
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.exercises.forEachIndexed { index, exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (index == state.currentExerciseIndex) {
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else if (state.completedExercises.contains(exercise.id)) {
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (state.completedExercises.contains(exercise.id)) {
                                        Icons.Filled.CheckCircle
                                    } else if (index == state.currentExerciseIndex) {
                                        Icons.Filled.CheckCircle
                                    } else {
                                        Icons.Outlined.Circle
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        state.completedExercises.contains(exercise.id) ->
                                            MaterialTheme.colorScheme.primary
                                        index == state.currentExerciseIndex ->
                                            MaterialTheme.colorScheme.primary
                                        else ->
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )

                                Text(
                                    text = "${exercise.id}. ${exercise.title}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Text(
                                text = "${exercise.durationSeconds} сек",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Exercise dialogs (reuse from Phase 2.2-2.4)
        if (state.isExerciseDialogOpen) {
            val currentExercise = state.exercises.getOrNull(state.currentExerciseIndex)

            if (currentExercise != null) {
                when (currentExercise.category) {
                    WarmupCategoryType.ARTICULATION -> {
                        currentExercise.articulationExercise?.let { exercise ->
                            ArticulationExerciseDialog(
                                exercise = exercise,
                                timerSeconds = exercise.durationSeconds,
                                isTimerRunning = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStartTimer = { /* Handle in local state */ },
                                onPauseTimer = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                }
                            )
                        }
                    }

                    WarmupCategoryType.BREATHING -> {
                        currentExercise.breathingExercise?.let { exercise ->
                            BreathingExerciseDialog(
                                exercise = exercise,
                                elapsedSeconds = 0,
                                totalSeconds = exercise.durationSeconds,
                                currentPhase = BreathingPhase.INHALE,
                                phaseProgress = 0f,
                                isRunning = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStart = { /* Handle in local state */ },
                                onPause = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                }
                            )
                        }
                    }

                    WarmupCategoryType.VOICE -> {
                        currentExercise.voiceExercise?.let { exercise ->
                            VoiceExerciseDialog(
                                exercise = exercise,
                                timerSeconds = exercise.durationSeconds,
                                isTimerRunning = false,
                                isAudioPlaying = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStartTimer = { /* Handle in local state */ },
                                onPauseTimer = { /* Handle in local state */ },
                                onPlayAudio = { /* Handle in local state */ },
                                onStopAudio = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                }
                            )
                        }
                    }

                    WarmupCategoryType.QUICK -> {
                        // Quick warmup не використовується всередині себе
                    }
                }
            }
        }

        // Completion dialog
        if (state.isCompleted) {
            CompletionDialog(
                totalExercises = state.exercises.size,
                elapsedSeconds = state.totalElapsedSeconds,
                onDismiss = {
                    viewModel.onEvent(QuickWarmupEvent.DismissCompletionDialog)
                    onNavigateBack()
                }
            )
        }
    }
}

@Composable
private fun CompletionDialog(
    totalExercises: Int,
    elapsedSeconds: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🎉", style = MaterialTheme.typography.displayMedium)
        },
        title = {
            Text("Розминка завершена!")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ви виконали $totalExercises вправи",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "за %d хв %02d сек".format(
                        elapsedSeconds / 60,
                        elapsedSeconds % 60
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Готово")
            }
        }
    )
}

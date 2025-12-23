package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.domain.model.exercise.StoryFormat
import com.aivoicepower.ui.screens.improvisation.components.StoryFormatCard
import com.aivoicepower.ui.screens.improvisation.components.StoryPromptCard
import com.aivoicepower.ui.screens.improvisation.components.StoryRecordingCard
import com.aivoicepower.ui.screens.improvisation.components.PreparationTimerCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorytellingScreen(
    viewModel: StorytellingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Auto-start preparation timer when format is selected
    LaunchedEffect(state.selectedFormat, state.isPreparationPhase) {
        if (state.selectedFormat != null && state.isPreparationPhase && state.preparationTimeLeft == 30) {
            viewModel.onEvent(StorytellingEvent.StartPreparation)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📖 Storytelling") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.selectedFormat == null -> {
                    // Format selection
                    Text(
                        text = "Обери формат історії:",
                        style = MaterialTheme.typography.titleLarge
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

                    if (state.preparationTimeLeft == 0) {
                        Button(
                            onClick = {
                                viewModel.onEvent(StorytellingEvent.StartRecording)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎤 Почати розповідь")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.ResetToFormatSelection)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("← Змінити формат")
                    }
                }

                state.isRecording -> {
                    // Recording phase
                    StoryRecordingCard(
                        format = state.selectedFormat!!,
                        prompt = state.storyPrompt,
                        durationMs = state.recordingDurationMs,
                        onStop = {
                            viewModel.onEvent(StorytellingEvent.StopRecording)
                        }
                    )
                }

                else -> {
                    // Recording completed
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "✓ Історія записана",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Тривалість: ${formatDuration(state.recordingDurationMs)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.CompleteTask)
                            state.recordingId?.let { recordingId ->
                                // For now, just go back since Results screen not yet ready for improvisation
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.recordingId != null
                    ) {
                        Text("Завершити")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.GenerateNewPrompt)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔄 Нова історія")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.ResetToFormatSelection)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("← Змінити формат")
                    }
                }
            }

            // Error message
            state.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

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
import com.aivoicepower.ui.screens.improvisation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomTopicScreen(
    viewModel: RandomTopicViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Auto-start preparation timer when topic is loaded
    LaunchedEffect(state.currentTopic) {
        if (state.currentTopic != null && state.isPreparationPhase && state.preparationTimeLeft == 15) {
            viewModel.onEvent(RandomTopicEvent.StartPreparation)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎲 Випадкова тема") },
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
                state.isPreparationPhase -> {
                    // Preparation phase
                    state.currentTopic?.let { topic ->
                        TopicDisplayCard(topic = topic)

                        PreparationTimerCard(
                            timeLeft = state.preparationTimeLeft,
                            onGenerateNew = {
                                viewModel.onEvent(RandomTopicEvent.GenerateNewTopic)
                            }
                        )
                    }

                    if (state.preparationTimeLeft == 0) {
                        Button(
                            onClick = {
                                viewModel.onEvent(RandomTopicEvent.StartRecording)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎤 Почати запис")
                        }
                    }
                }

                state.isRecording -> {
                    // Recording phase
                    state.currentTopic?.let { topic ->
                        RandomTopicRecordingCard(
                            topic = topic,
                            durationMs = state.recordingDurationMs,
                            onStop = {
                                viewModel.onEvent(RandomTopicEvent.StopRecording)
                            }
                        )
                    }
                }

                else -> {
                    // Recording completed
                    state.currentTopic?.let { topic ->
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
                                    text = "✓ Запис завершено",
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
                                viewModel.onEvent(RandomTopicEvent.CompleteTask)
                                // Navigate to placeholder results (TODO: implement actual results screen)
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
                                viewModel.onEvent(RandomTopicEvent.GenerateNewTopic)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔄 Нова тема")
                        }
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

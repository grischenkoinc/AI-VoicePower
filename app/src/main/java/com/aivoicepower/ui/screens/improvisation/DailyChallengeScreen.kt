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
import com.aivoicepower.ui.screens.improvisation.components.DailyChallengeCard
import com.aivoicepower.ui.screens.improvisation.components.DailyChallengeRecordingCard
import com.aivoicepower.ui.screens.improvisation.components.PreparationTimerCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    viewModel: DailyChallengeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Auto-start preparation timer when challenge is loaded
    LaunchedEffect(state.challenge, state.isPreparationPhase) {
        if (state.challenge != null && state.isPreparationPhase &&
            state.preparationTimeLeft == 20 && !state.isCompleted) {
            viewModel.onEvent(DailyChallengeEvent.StartPreparation)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏆 Daily Challenge") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.isCompleted -> {
                    // Already completed today
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.challenge?.let { challenge ->
                            DailyChallengeCard(
                                challenge = challenge,
                                isCompleted = true
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "✓ Виклик виконано!",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Тривалість: ${formatDuration(state.recordingDurationMs)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Повертайся завтра за новим викликом!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            val today = LocalDate.now()
                            val tomorrow = today.plusDays(1)
                            val formatter = DateTimeFormatter.ofPattern("dd MMMM")

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Наступний виклик:",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = tomorrow.format(formatter),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.challenge?.let { challenge ->
                            when {
                                state.isPreparationPhase -> {
                                    // Preparation phase
                                    DailyChallengeCard(
                                        challenge = challenge,
                                        isCompleted = false
                                    )

                                    PreparationTimerCard(
                                        timeLeft = state.preparationTimeLeft,
                                        onGenerateNew = null // Can't regenerate daily challenge
                                    )

                                    if (state.preparationTimeLeft == 0) {
                                        Button(
                                            onClick = {
                                                viewModel.onEvent(DailyChallengeEvent.StartRecording)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("🎤 Почати виклик")
                                        }
                                    }
                                }

                                state.isRecording -> {
                                    // Recording phase
                                    DailyChallengeRecordingCard(
                                        challenge = challenge,
                                        durationMs = state.recordingDurationMs,
                                        onStop = {
                                            viewModel.onEvent(DailyChallengeEvent.StopRecording)
                                        }
                                    )
                                }

                                else -> {
                                    // Recording completed but not yet saved
                                    DailyChallengeCard(
                                        challenge = challenge,
                                        isCompleted = false
                                    )

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
                                            viewModel.onEvent(DailyChallengeEvent.CompleteChallenge)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = state.recordingId != null
                                    ) {
                                        Text("Завершити виклик")
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
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

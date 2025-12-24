package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebateScreen(
    viewModel: DebateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚔️ Дебати з AI") },
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
            when (state.phase) {
                DebatePhase.TopicSelection -> {
                    TopicSelectionContent(
                        onTopicSelected = { viewModel.onEvent(DebateEvent.TopicSelected(it)) }
                    )
                }

                DebatePhase.PositionSelection -> {
                    PositionSelectionContent(
                        topic = state.selectedTopic!!,
                        onPositionSelected = { viewModel.onEvent(DebateEvent.PositionSelected(it)) }
                    )
                }

                DebatePhase.UserArgument -> {
                    UserArgumentContent(
                        topic = state.selectedTopic!!,
                        position = state.userPosition!!,
                        roundNumber = state.currentRound,
                        isRecording = state.isRecording,
                        secondsElapsed = state.recordingSeconds,
                        maxSeconds = state.maxRecordingSeconds,
                        onStartRecording = { viewModel.onEvent(DebateEvent.StartRecordingClicked) },
                        onStopRecording = { viewModel.onEvent(DebateEvent.StopRecordingClicked) }
                    )
                }

                DebatePhase.AiResponse -> {
                    AiResponseContent(
                        isThinking = state.isAiThinking,
                        rounds = state.rounds,
                        currentRound = state.currentRound,
                        maxRounds = state.maxRounds,
                        onNextRound = { viewModel.onEvent(DebateEvent.NextRoundClicked) },
                        onFinish = { viewModel.onEvent(DebateEvent.FinishDebateClicked) }
                    )
                }

                DebatePhase.DebateComplete -> {
                    DebateCompleteContent(
                        topic = state.selectedTopic!!,
                        rounds = state.rounds,
                        onFinish = onNavigateBack
                    )
                }
            }

            // Error message
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicSelectionContent(
    onTopicSelected: (com.aivoicepower.data.content.DebateTopicsProvider.DebateTopic) -> Unit
) {
    val topics = remember { com.aivoicepower.data.content.DebateTopicsProvider().getAllTopics() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Обери тему для дебатів:",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(topics) { topic ->
            Card(
                onClick = { onTopicSelected(topic) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = topic.topic,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = topic.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Складність: ${topic.difficulty}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PositionSelectionContent(
    topic: com.aivoicepower.data.content.DebateTopicsProvider.DebateTopic,
    onPositionSelected: (DebatePosition) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Тема:",
            style = MaterialTheme.typography.titleMedium
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = topic.topic,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Обери свою позицію:",
            style = MaterialTheme.typography.titleLarge
        )

        Card(
            onClick = { onPositionSelected(DebatePosition.FOR) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✅ ЗА",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Ти будеш підтримувати цю позицію",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(
            onClick = { onPositionSelected(DebatePosition.AGAINST) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "❌ ПРОТИ",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Ти будеш аргументувати проти",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun UserArgumentContent(
    topic: com.aivoicepower.data.content.DebateTopicsProvider.DebateTopic,
    position: DebatePosition,
    roundNumber: Int,
    isRecording: Boolean,
    secondsElapsed: Int,
    maxSeconds: Int,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Раунд $roundNumber з 5",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = topic.topic,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Твоя позиція: ${if (position == DebatePosition.FOR) "ЗА" else "ПРОТИ"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (isRecording) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🔴 Запис аргументу...",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "$secondsElapsed / $maxSeconds сек",
                        style = MaterialTheme.typography.displayMedium
                    )
                    LinearProgressIndicator(
                        progress = secondsElapsed.toFloat() / maxSeconds,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = onStopRecording,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("■ Завершити запис")
                    }
                }
            }
        } else {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Твій хід:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Наведи свій аргумент. У тебе є до $maxSeconds секунд.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = onStartRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎤 Почати запис аргументу")
            }
        }
    }
}

@Composable
private fun AiResponseContent(
    isThinking: Boolean,
    rounds: List<DebateRound>,
    currentRound: Int,
    maxRounds: Int,
    onNextRound: () -> Unit,
    onFinish: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isThinking) {
            item {
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "AI обдумує відповідь...",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "Раунд $currentRound:",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            rounds.lastOrNull()?.let { lastRound ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Твій аргумент:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = lastRound.userArgument,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Відповідь AI:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = lastRound.aiResponse,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            item {
                if (currentRound < maxRounds) {
                    Button(
                        onClick = onNextRound,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Наступний раунд (${currentRound + 1}/$maxRounds)")
                    }
                } else {
                    Button(
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Завершити дебати")
                    }
                }
            }
        }
    }
}

@Composable
private fun DebateCompleteContent(
    topic: com.aivoicepower.data.content.DebateTopicsProvider.DebateTopic,
    rounds: List<DebateRound>,
    onFinish: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
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
                        text = "✓ Дебати завершено!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Тема: ${topic.topic}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Проведено ${rounds.size} раундів",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Text(
                text = "Історія дебатів:",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(rounds) { round ->
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Раунд ${round.roundNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ти: ${round.userArgument}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Divider()
                    Text(
                        text = "AI: ${round.aiResponse}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Готово")
            }
        }
    }
}

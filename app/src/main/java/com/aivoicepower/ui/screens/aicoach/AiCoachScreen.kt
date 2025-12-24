package com.aivoicepower.ui.screens.aicoach

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.ui.screens.aicoach.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCoachScreen(
    viewModel: AiCoachViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPremium: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    // Permission launcher for microphone
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(AiCoachEvent.StartVoiceInput)
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(AiCoachEvent.AudioFileSelected(it))
        }
    }

    // Auto-scroll to bottom when new message
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size)
        }
    }

    // Show error as snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(AiCoachEvent.ErrorDismissed)
        }
    }

    // Show scenario dialog
    if (state.showScenarioDialog) {
        ScenarioDialog(
            scenarios = viewModel.getSimulationScenarios(),
            onScenarioSelected = { scenario ->
                viewModel.onEvent(AiCoachEvent.StartSimulation(scenario))
            },
            onDismiss = {
                viewModel.onEvent(AiCoachEvent.HideScenarioDialog)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Тренер")
                        if (state.activeSimulation != null) {
                            Text(
                                text = "Симуляція: ${state.activeSimulation!!.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else if (!state.isPremium) {
                            Text(
                                text = "Повідомлень: ${state.messagesRemaining}/10",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    // Simulation exit button
                    if (state.activeSimulation != null) {
                        IconButton(
                            onClick = { viewModel.onEvent(AiCoachEvent.ExitSimulation) }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Завершити симуляцію")
                        }
                    }

                    // Menu
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Симуляція") },
                                onClick = {
                                    showMenu = false
                                    viewModel.onEvent(AiCoachEvent.ShowScenarioDialog)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Експортувати") },
                                onClick = {
                                    showMenu = false
                                    viewModel.onEvent(AiCoachEvent.ExportConversation)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                },
                                enabled = state.messages.isNotEmpty()
                            )
                            if (state.messages.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Очистити") },
                                    onClick = {
                                        showMenu = false
                                        viewModel.onEvent(AiCoachEvent.ClearConversationClicked)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, contentDescription = null)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Messages
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (state.messages.isEmpty() && !state.isLoading) {
                    EmptyStateContentWithTemplates(
                        quickActions = state.quickActions,
                        templates = state.templates,
                        onQuickActionClick = { action ->
                            viewModel.onEvent(AiCoachEvent.QuickActionClicked(action))
                        },
                        onTemplateClick = { template ->
                            viewModel.onEvent(AiCoachEvent.ApplyTemplate(template))
                        },
                        onSimulationClick = {
                            viewModel.onEvent(AiCoachEvent.ShowScenarioDialog)
                        }
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.messages) { message ->
                            when (message.role) {
                                MessageRole.USER -> {
                                    UserMessageBubble(
                                        message = message.content,
                                        timestamp = message.timestamp
                                    )
                                }
                                MessageRole.ASSISTANT -> {
                                    AssistantMessageBubble(
                                        message = message.content,
                                        timestamp = message.timestamp
                                    )
                                }
                                MessageRole.SYSTEM -> {
                                    // Skip system messages in UI
                                }
                            }
                        }

                        // Typing indicator
                        if (state.isSending || state.isUploadingAudio) {
                            item {
                                TypingIndicator()
                            }
                        }
                    }
                }
            }

            // Simulation progress indicator
            if (state.activeSimulation != null) {
                SimulationProgressBar(
                    currentStep = state.simulationStep + 1,
                    totalSteps = state.activeSimulation!!.steps.size
                )
            }

            // Quick Actions Bar
            if (state.quickActions.isNotEmpty() && state.messages.isNotEmpty() && state.activeSimulation == null) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.quickActions) { action ->
                        QuickActionChip(
                            text = action,
                            onClick = {
                                viewModel.onEvent(AiCoachEvent.QuickActionClicked(action))
                            }
                        )
                    }
                }
            }

            // Free Tier Warning
            if (!state.isPremium && state.messagesRemaining <= 3 && state.messagesRemaining > 0) {
                FreeTierBanner(
                    messagesRemaining = state.messagesRemaining,
                    onUpgradeClick = onNavigateToPremium
                )
            }

            // Input Area
            ChatInputField(
                text = state.inputText,
                onTextChange = { viewModel.onEvent(AiCoachEvent.InputChanged(it)) },
                onSendClick = { viewModel.onEvent(AiCoachEvent.SendMessageClicked) },
                onVoiceInputClick = {
                    if (state.isListening) {
                        viewModel.onEvent(AiCoachEvent.StopVoiceInput)
                    } else {
                        // Check permission
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                viewModel.onEvent(AiCoachEvent.StartVoiceInput)
                            }
                            else -> {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    }
                },
                onUploadClick = {
                    filePickerLauncher.launch("audio/*")
                },
                enabled = state.canSendMessage && !state.isSending && !state.isUploadingAudio,
                isLoading = state.isSending || state.isUploadingAudio,
                isListening = state.isListening
            )
        }
    }
}

@Composable
private fun EmptyStateContentWithTemplates(
    quickActions: List<String>,
    templates: List<ConversationTemplate>,
    onQuickActionClick: (String) -> Unit,
    onTemplateClick: (ConversationTemplate) -> Unit,
    onSimulationClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "\uD83D\uDC4B",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Вітаю! Я твій AI-тренер",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Запитай мене про покращення мовлення або обери шаблон нижче",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Simulation button
        item {
            OutlinedButton(
                onClick = onSimulationClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Почати симуляцію")
            }
        }

        // Templates
        if (templates.isNotEmpty()) {
            item {
                Text(
                    text = "Шаблони",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(templates) { template ->
                TemplateCard(
                    template = template,
                    onClick = { onTemplateClick(template) }
                )
            }
        }

        // Quick actions
        if (quickActions.isNotEmpty()) {
            item {
                Text(
                    text = "Швидкі дії",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(quickActions) { action ->
                SuggestionChip(
                    onClick = { onQuickActionClick(action) },
                    label = { Text(action) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateCard(
    template: ConversationTemplate,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = template.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = template.title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SimulationProgressBar(
    currentStep: Int,
    totalSteps: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Крок $currentStep з $totalSteps",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${(currentStep.toFloat() / totalSteps * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        @Suppress("DEPRECATION")
        LinearProgressIndicator(
            progress = currentStep.toFloat() / totalSteps,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
    }
}

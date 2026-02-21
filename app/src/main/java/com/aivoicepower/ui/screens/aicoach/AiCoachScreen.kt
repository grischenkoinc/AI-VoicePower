package com.aivoicepower.ui.screens.aicoach

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.ui.screens.aicoach.components.*
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.modifiers.staggeredEntry

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
    val view = LocalView.current
    var showMenu by remember { mutableStateOf(false) }

    // TTS state
    val isTtsSpeaking by viewModel.ttsManager.isSpeaking.collectAsStateWithLifecycle()
    var speakingMessageId by remember { mutableStateOf<String?>(null) }

    // Clear speakingMessageId when TTS stops
    LaunchedEffect(isTtsSpeaking) {
        if (!isTtsSpeaking) {
            speakingMessageId = null
        }
    }

    // Stop TTS when navigating away
    DisposableEffect(Unit) {
        onDispose {
            viewModel.ttsManager.stop()
        }
    }

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

    // Voice click handler
    val onVoiceClick: () -> Unit = {
        if (state.isListening) {
            viewModel.onEvent(AiCoachEvent.StopVoiceInput)
        } else {
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
    }

    GradientBackground(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // ===== TOP BAR =====
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.2f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .statusBarsPadding()
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onNavigateBack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Назад",
                                    tint = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AI Тренер",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White
                                )
                                if (state.activeSimulation != null) {
                                    Text(
                                        text = "Симуляція: ${state.activeSimulation!!.title}",
                                        style = TextStyle(fontSize = 12.sp),
                                        color = PrimaryColors.light
                                    )
                                } else if (!state.isPremium) {
                                    Text(
                                        text = "Повідомлень: ${state.messagesRemaining}/10",
                                        style = TextStyle(fontSize = 12.sp),
                                        color = TextColors.onDarkMuted
                                    )
                                }
                            }

                            if (state.activeSimulation != null) {
                                IconButton(
                                    onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); viewModel.onEvent(AiCoachEvent.ExitSimulation) }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Завершити симуляцію",
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Box {
                                IconButton(onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); showMenu = true }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Меню",
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
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
                    }

                    // ===== MESSAGES LIST (scrollable, takes available space) =====
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (state.messages.isEmpty() && !state.isLoading) {
                            // Empty state with mic prompt
                            EmptyVoiceState(
                                templates = state.templates,
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
                                contentPadding = PaddingValues(
                                    start = 12.dp,
                                    end = 12.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.messages) { message ->
                                    val isExpanded = message.id in state.expandedMessageIds

                                    when (message.role) {
                                        MessageRole.USER -> {
                                            UserMessageBubble(
                                                message = message.content,
                                                timestamp = message.timestamp,
                                                isExpanded = isExpanded,
                                                onToggleExpand = {
                                                    viewModel.onEvent(
                                                        AiCoachEvent.ToggleMessageExpanded(message.id)
                                                    )
                                                }
                                            )
                                        }
                                        MessageRole.ASSISTANT -> {
                                            AssistantMessageBubble(
                                                message = message.content,
                                                timestamp = message.timestamp,
                                                isExpanded = isExpanded,
                                                isSpeakingThis = isTtsSpeaking && speakingMessageId == message.id,
                                                onToggleExpand = {
                                                    viewModel.onEvent(
                                                        AiCoachEvent.ToggleMessageExpanded(message.id)
                                                    )
                                                },
                                                onSpeakClick = {
                                                    if (isTtsSpeaking && speakingMessageId == message.id) {
                                                        viewModel.ttsManager.stop()
                                                        speakingMessageId = null
                                                    } else {
                                                        viewModel.ttsManager.speak(message.content)
                                                        speakingMessageId = message.id
                                                    }
                                                }
                                            )
                                        }
                                        MessageRole.SYSTEM -> {
                                            // Skip system messages
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

                    // ===== SIMULATION PROGRESS =====
                    if (state.activeSimulation != null) {
                        SimulationProgressBar(
                            currentStep = state.simulationStep + 1,
                            totalSteps = state.activeSimulation!!.steps.size
                        )
                    }

                    // ===== QUICK ACTIONS (above mic) =====
                    if (state.quickActions.isNotEmpty() && state.messages.isNotEmpty() && state.activeSimulation == null) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(state.quickActions) { index, action ->
                                QuickActionChip(
                                    text = action,
                                    onClick = {
                                        viewModel.onEvent(AiCoachEvent.QuickActionClicked(action))
                                    },
                                    modifier = Modifier.staggeredEntry(
                                        index = index,
                                        staggerDelay = 60
                                    )
                                )
                            }
                        }
                    }

                    // ===== FREE TIER WARNING =====
                    if (!state.isPremium && state.messagesRemaining <= 3 && state.messagesRemaining > 0) {
                        FreeTierBanner(
                            messagesRemaining = state.messagesRemaining,
                            onUpgradeClick = onNavigateToPremium
                        )
                    }

                    // ===== VOICE MIC BUTTON (primary action) =====
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        VoiceMicButton(
                            isListening = state.isListening,
                            isSending = state.isSending,
                            audioLevel = state.audioLevel,
                            onClick = onVoiceClick
                        )
                    }

                    // ===== COMPACT TEXT INPUT (secondary) =====
                    ChatInputField(
                        text = state.inputText,
                        onTextChange = { viewModel.onEvent(AiCoachEvent.InputChanged(it)) },
                        onSendClick = { viewModel.onEvent(AiCoachEvent.SendMessageClicked) },
                        onUploadClick = {
                            filePickerLauncher.launch("audio/*")
                        },
                        enabled = state.canSendMessage && !state.isSending && !state.isUploadingAudio && !state.isListening,
                        isLoading = state.isSending || state.isUploadingAudio
                    )

                    // Bottom nav bar padding
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }

                // Snackbar
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 120.dp)
                )
            }
        }
    )
}

// ===== EMPTY STATE =====

@Composable
private fun EmptyVoiceState(
    templates: List<ConversationTemplate>,
    onTemplateClick: (ConversationTemplate) -> Unit,
    onSimulationClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))

            // AI Avatar with glow
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(20.dp, CircleShape, spotColor = PrimaryColors.glow.copy(alpha = glowAlpha))
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6), Color(0xFF764BA2))
                            )
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83E\uDD16", fontSize = 36.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Привіт! Я твій AI-тренер",
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Натисніть мікрофон, щоб почати розмову",
                style = TextStyle(fontSize = 14.sp),
                color = TextColors.onDarkMuted,
                textAlign = TextAlign.Center
            )
        }

        // Simulation button
        item {
            val btnShape = RoundedCornerShape(14.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(btnShape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, PrimaryColors.light.copy(alpha = 0.25f), btnShape)
                    .clickable(onClick = onSimulationClick)
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = PrimaryColors.light
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Почати симуляцію",
                        color = Color.White.copy(alpha = 0.8f),
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    )
                }
            }
        }

        // Templates
        if (templates.isNotEmpty()) {
            item {
                Text(
                    text = "Шаблони",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
            }

            items(templates) { template ->
                GradientTemplateCard(
                    template = template,
                    onClick = { onTemplateClick(template) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ===== TEMPLATE CARD =====

@Composable
private fun GradientTemplateCard(
    template: ConversationTemplate,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.White.copy(alpha = 0.07f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), cardShape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryColors.default.copy(alpha = 0.15f))
                .border(1.dp, PrimaryColors.light.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = template.emoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = template.title,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

// ===== SIMULATION PROGRESS =====

@Composable
private fun SimulationProgressBar(
    currentStep: Int,
    totalSteps: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Крок $currentStep з $totalSteps",
                style = TextStyle(fontSize = 11.sp),
                color = PrimaryColors.light
            )
            Text(
                text = "${(currentStep.toFloat() / totalSteps * 100).toInt()}%",
                style = TextStyle(fontSize = 11.sp),
                color = PrimaryColors.light
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(currentStep.toFloat() / totalSteps)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6))
                        )
                    )
            )
        }
    }
}

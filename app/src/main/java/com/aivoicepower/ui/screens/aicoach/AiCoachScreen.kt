package com.aivoicepower.ui.screens.aicoach

import android.Manifest
import android.content.pm.PackageManager
import android.view.HapticFeedbackConstants
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
    var showMenu by remember { mutableStateOf(false) }

    // TTS state
    val isTtsSpeaking by viewModel.ttsManager.isSpeaking.collectAsStateWithLifecycle()
    var speakingMessageId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isTtsSpeaking) {
        if (!isTtsSpeaking) speakingMessageId = null
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.ttsManager.stop() }
    }

    // Permission launcher
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.onEvent(AiCoachEvent.StartVoiceInput)
    }

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onEvent(AiCoachEvent.AudioFileSelected(it)) } }

    // Auto-scroll to bottom
    val totalItems = state.messages.size +
            (if (state.isSending || state.isUploadingAudio) 1 else 0) +
            (if (state.quickActions.isNotEmpty() && state.messages.isNotEmpty() && state.activeSimulation == null) 1 else 0)
    LaunchedEffect(totalItems) {
        if (totalItems > 0) listState.animateScrollToItem(totalItems - 1)
    }

    // Error snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(message = error, duration = SnackbarDuration.Short)
            viewModel.onEvent(AiCoachEvent.ErrorDismissed)
        }
    }

    // Scenario dialog
    if (state.showScenarioDialog) {
        ScenarioDialog(
            scenarios = viewModel.getSimulationScenarios(),
            onScenarioSelected = { viewModel.onEvent(AiCoachEvent.StartSimulation(it)) },
            onDismiss = { viewModel.onEvent(AiCoachEvent.HideScenarioDialog) }
        )
    }

    // Voice click handler
    val onVoiceClick: () -> Unit = {
        if (state.isListening) {
            viewModel.onEvent(AiCoachEvent.StopVoiceInput)
        } else {
            when {
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED -> {
                    viewModel.onEvent(AiCoachEvent.StartVoiceInput)
                }
                else -> micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    GradientBackground(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // ===== TOP BAR =====
                    CoachTopBar(
                        activeSimulation = state.activeSimulation,
                        isPremium = state.isPremium,
                        messagesRemaining = state.messagesRemaining,
                        showMenu = showMenu,
                        onShowMenuChange = { showMenu = it },
                        onNavigateBack = onNavigateBack,
                        onExitSimulation = { viewModel.onEvent(AiCoachEvent.ExitSimulation) },
                        onShowScenarioDialog = { viewModel.onEvent(AiCoachEvent.ShowScenarioDialog) },
                        onExportConversation = { viewModel.onEvent(AiCoachEvent.ExportConversation) },
                        onClearConversation = { viewModel.onEvent(AiCoachEvent.ClearConversationClicked) },
                        hasMessages = state.messages.isNotEmpty()
                    )

                    // ===== MESSAGES AREA =====
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (state.messages.isEmpty() && !state.isLoading) {
                            CoachEmptyState(
                                templates = state.templates,
                                onTemplateClick = { viewModel.onEvent(AiCoachEvent.ApplyTemplate(it)) }
                            )
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 12.dp, end = 12.dp,
                                    top = 8.dp, bottom = 8.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Simulation progress
                                if (state.activeSimulation != null) {
                                    item {
                                        SimulationProgressBar(
                                            currentStep = state.simulationStep + 1,
                                            totalSteps = state.activeSimulation!!.steps.size
                                        )
                                    }
                                }

                                // Messages
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
                                                timestamp = message.timestamp,
                                                isSpeakingThis = isTtsSpeaking && speakingMessageId == message.id,
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
                                        MessageRole.SYSTEM -> { /* skip */ }
                                    }
                                }

                                // Typing indicator
                                if (state.isSending || state.isUploadingAudio) {
                                    item { TypingIndicator() }
                                }

                                // Quick actions (inside scroll)
                                if (state.quickActions.isNotEmpty() && state.messages.isNotEmpty() && state.activeSimulation == null) {
                                    item {
                                        LazyRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(vertical = 4.dp)
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
                                }
                            }
                        }
                    }

                    // ===== SLIM FREE TIER BANNER =====
                    if (!state.isPremium && state.messagesRemaining in 1..3) {
                        SlimFreeTierBanner(
                            messagesRemaining = state.messagesRemaining,
                            onUpgradeClick = onNavigateToPremium
                        )
                    }

                    // ===== UNIFIED INPUT BAR =====
                    UnifiedInputBar(
                        text = state.inputText,
                        onTextChange = { viewModel.onEvent(AiCoachEvent.InputChanged(it)) },
                        onSendClick = { viewModel.onEvent(AiCoachEvent.SendMessageClicked) },
                        onVoiceClick = onVoiceClick,
                        onAttachClick = { filePickerLauncher.launch("audio/*") },
                        isListening = state.isListening,
                        isSending = state.isSending,
                        isUploadingAudio = state.isUploadingAudio,
                        audioLevel = state.audioLevel,
                        enabled = state.canSendMessage && !state.isSending && !state.isUploadingAudio && !state.isListening
                    )

                    Spacer(modifier = Modifier.navigationBarsPadding())
                }

                // Snackbar
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                )
            }
        }
    )
}

// ===== TOP BAR =====

@Composable
private fun CoachTopBar(
    activeSimulation: SimulationScenario?,
    isPremium: Boolean,
    messagesRemaining: Int,
    showMenu: Boolean,
    onShowMenuChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onExitSimulation: () -> Unit,
    onShowScenarioDialog: () -> Unit,
    onExportConversation: () -> Unit,
    onClearConversation: () -> Unit,
    hasMessages: Boolean
) {
    val view = LocalView.current

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
            IconButton(onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onNavigateBack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Тренер",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
                if (activeSimulation != null) {
                    Text(
                        text = "Симуляція: ${activeSimulation.title}",
                        style = TextStyle(fontSize = 12.sp),
                        color = PrimaryColors.light
                    )
                } else if (!isPremium) {
                    val dotColor = when {
                        messagesRemaining > 5 -> Color(0xFF10B981)
                        messagesRemaining > 2 -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$messagesRemaining повідомлень",
                            style = TextStyle(fontSize = 12.sp),
                            color = TextColors.onDarkMuted
                        )
                    }
                }
            }

            if (activeSimulation != null) {
                IconButton(onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onExitSimulation()
                }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Завершити симуляцію",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Box {
                IconButton(onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onShowMenuChange(true)
                }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Меню",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { onShowMenuChange(false) }
                ) {
                    DropdownMenuItem(
                        text = { Text("Симуляція") },
                        onClick = {
                            onShowMenuChange(false)
                            onShowScenarioDialog()
                        },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Експортувати") },
                        onClick = {
                            onShowMenuChange(false)
                            onExportConversation()
                        },
                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                        enabled = hasMessages
                    )
                    if (hasMessages) {
                        DropdownMenuItem(
                            text = { Text("Очистити") },
                            onClick = {
                                onShowMenuChange(false)
                                onClearConversation()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}

// ===== EMPTY STATE =====

@Composable
private fun CoachEmptyState(
    templates: List<ConversationTemplate>,
    onTemplateClick: (ConversationTemplate) -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Small AI avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(12.dp, CircleShape, spotColor = PrimaryColors.glow.copy(alpha = glowAlpha))
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6), Color(0xFF764BA2))
                    )
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "\uD83E\uDD16", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Чим можу допомогти?",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2x2 suggestion grid
        val displayTemplates = templates.take(4)
        if (displayTemplates.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (rowIndex in displayTemplates.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SuggestionCard(
                            template = displayTemplates[rowIndex],
                            onClick = { onTemplateClick(displayTemplates[rowIndex]) },
                            modifier = Modifier.weight(1f)
                        )
                        if (rowIndex + 1 < displayTemplates.size) {
                            SuggestionCard(
                                template = displayTemplates[rowIndex + 1],
                                onClick = { onTemplateClick(displayTemplates[rowIndex + 1]) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionCard(
    template: ConversationTemplate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(Color.White.copy(alpha = 0.06f), cardShape)
            .border(1.dp, Color.White.copy(alpha = 0.10f), cardShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryColors.default.copy(alpha = 0.12f))
                .border(1.dp, PrimaryColors.light.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = template.emoji, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = template.title,
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 2
        )
    }
}

// ===== SLIM FREE TIER BANNER =====

@Composable
private fun SlimFreeTierBanner(
    messagesRemaining: Int,
    onUpgradeClick: () -> Unit
) {
    val view = LocalView.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF59E0B).copy(alpha = 0.08f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "\u26A0\uFE0F",
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Залишилось $messagesRemaining повідомлень",
            style = TextStyle(fontSize = 12.sp),
            color = Color.White.copy(alpha = 0.75f)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Оновити",
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = PrimaryColors.light,
            modifier = Modifier.clickable {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onUpgradeClick()
            }
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
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
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
        Spacer(modifier = Modifier.height(6.dp))
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

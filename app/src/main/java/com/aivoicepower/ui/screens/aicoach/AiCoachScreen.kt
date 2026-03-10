package com.aivoicepower.ui.screens.aicoach

import android.Manifest
import android.content.pm.PackageManager
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.chat.Message
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.ui.screens.aicoach.components.*
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.modifiers.staggeredEntry
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ===== ORB STATE =====

private enum class OrbState { IDLE, LISTENING, PROCESSING, SPEAKING }

// ===== MAIN SCREEN =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCoachScreen(
    viewModel: AiCoachViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPremium: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var transcriptExpanded by remember { mutableStateOf(false) }

    // TTS state
    val isTtsSpeaking by viewModel.ttsManager.isSpeaking.collectAsStateWithLifecycle()
    var speakingMessageId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isTtsSpeaking) {
        if (!isTtsSpeaking) speakingMessageId = null
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.ttsManager.stop() }
    }

    // Orb state
    val orbState = when {
        state.isListening -> OrbState.LISTENING
        state.isSending || state.isUploadingAudio -> OrbState.PROCESSING
        isTtsSpeaking -> OrbState.SPEAKING
        else -> OrbState.IDLE
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

    // Orb click
    val onOrbClick: () -> Unit = {
        when (orbState) {
            OrbState.IDLE -> {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.onEvent(AiCoachEvent.StartVoiceInput)
                } else {
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
            OrbState.LISTENING -> viewModel.onEvent(AiCoachEvent.StopVoiceInput)
            OrbState.SPEAKING -> viewModel.ttsManager.stop()
            OrbState.PROCESSING -> { /* no-op */ }
        }
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

                    // ===== MAIN ORB AREA =====
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // Orb + status centered
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = if (state.messages.isNotEmpty()) 56.dp else 0.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            VoiceOrb(
                                orbState = orbState,
                                onClick = onOrbClick,
                                modifier = Modifier.size(250.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Status text
                            Text(
                                text = when (orbState) {
                                    OrbState.IDLE -> if (state.messages.isEmpty())
                                        "Натисніть, щоб говорити" else "Торкніться для запису"
                                    OrbState.LISTENING -> "Слухаю..."
                                    OrbState.PROCESSING -> "Обробка..."
                                    OrbState.SPEAKING -> "Говорю..."
                                },
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = when (orbState) {
                                    OrbState.LISTENING -> Color(0xFFEF4444).copy(alpha = 0.8f)
                                    OrbState.SPEAKING -> Color(0xFF10B981).copy(alpha = 0.8f)
                                    else -> Color.White.copy(alpha = 0.5f)
                                },
                                textAlign = TextAlign.Center
                            )

                            // Suggestions (empty state only)
                            if (state.messages.isEmpty() && state.templates.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(32.dp))
                                SuggestionGrid(
                                    templates = state.templates.take(4),
                                    onTemplateClick = {
                                        viewModel.onEvent(AiCoachEvent.ApplyTemplate(it))
                                    }
                                )
                            }
                        }

                        // Transcript panel (bottom overlay)
                        if (state.messages.isNotEmpty()) {
                            TranscriptPanel(
                                messages = state.messages,
                                expanded = transcriptExpanded,
                                onToggle = { transcriptExpanded = !transcriptExpanded },
                                isSending = state.isSending || state.isUploadingAudio,
                                isTtsSpeaking = isTtsSpeaking,
                                speakingMessageId = speakingMessageId,
                                onSpeakMessage = { msg ->
                                    if (isTtsSpeaking && speakingMessageId == msg.id) {
                                        viewModel.ttsManager.stop()
                                        speakingMessageId = null
                                    } else {
                                        viewModel.ttsManager.speak(msg.content)
                                        speakingMessageId = msg.id
                                    }
                                },
                                quickActions = state.quickActions,
                                onQuickAction = {
                                    viewModel.onEvent(AiCoachEvent.QuickActionClicked(it))
                                },
                                activeSimulation = state.activeSimulation,
                                simulationStep = state.simulationStep,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }

                    // ===== FREE TIER BANNER =====
                    if (!state.isPremium && state.messagesRemaining in 1..3) {
                        SlimFreeTierBanner(
                            messagesRemaining = state.messagesRemaining,
                            onUpgradeClick = onNavigateToPremium
                        )
                    }

                    // ===== COMPACT TEXT INPUT =====
                    CompactInputRow(
                        text = state.inputText,
                        onTextChange = { viewModel.onEvent(AiCoachEvent.InputChanged(it)) },
                        onSendClick = { viewModel.onEvent(AiCoachEvent.SendMessageClicked) },
                        enabled = state.canSendMessage && !state.isSending
                                && !state.isUploadingAudio && !state.isListening
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

// ===== VOICE ORB (GPT-style animated sphere) =====

@Composable
private fun VoiceOrb(
    orbState: OrbState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val infiniteTransition = rememberInfiniteTransition(label = "orb")

    // === Animated colors (light pastel palette) ===
    val color1 by animateColorAsState(
        targetValue = when (orbState) {
            OrbState.IDLE -> Color(0xFFB4C6FC)
            OrbState.LISTENING -> Color(0xFFFCA5A5)
            OrbState.PROCESSING -> Color(0xFFC4B5FD)
            OrbState.SPEAKING -> Color(0xFF86EFAC)
        },
        animationSpec = tween(800), label = "c1"
    )
    val color2 by animateColorAsState(
        targetValue = when (orbState) {
            OrbState.IDLE -> Color(0xFFC4B5FD)
            OrbState.LISTENING -> Color(0xFFF9A8D4)
            OrbState.PROCESSING -> Color(0xFF7DD3FC)
            OrbState.SPEAKING -> Color(0xFF7DD3FC)
        },
        animationSpec = tween(800), label = "c2"
    )
    val color3 by animateColorAsState(
        targetValue = when (orbState) {
            OrbState.IDLE -> Color(0xFFD8B4FE)
            OrbState.LISTENING -> Color(0xFFFDE68A)
            OrbState.PROCESSING -> Color(0xFFB4C6FC)
            OrbState.SPEAKING -> Color(0xFF93C5FD)
        },
        animationSpec = tween(800), label = "c3"
    )

    // === Blob rotation (continuous) ===
    val angle1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(6000, easing = LinearEasing), RepeatMode.Restart
        ), label = "a1"
    )
    val angle2 by infiniteTransition.animateFloat(
        initialValue = 120f, targetValue = -240f,
        animationSpec = infiniteRepeatable(
            tween(8000, easing = LinearEasing), RepeatMode.Restart
        ), label = "a2"
    )
    val angle3 by infiniteTransition.animateFloat(
        initialValue = 240f, targetValue = 600f,
        animationSpec = infiniteRepeatable(
            tween(10000, easing = LinearEasing), RepeatMode.Restart
        ), label = "a3"
    )

    // === Breathing ===
    val basePulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "pulse"
    )
    val pulseAmplitude by animateFloatAsState(
        targetValue = when (orbState) {
            OrbState.IDLE -> 0.03f
            OrbState.LISTENING -> 0.10f
            OrbState.PROCESSING -> 0.015f
            OrbState.SPEAKING -> 0.05f
        },
        animationSpec = tween(600), label = "amp"
    )
    val breathScale = 1f + basePulse * pulseAmplitude

    // === Blob movement amplitude ===
    val blobAmplitude by animateFloatAsState(
        targetValue = when (orbState) {
            OrbState.IDLE -> 0.25f
            OrbState.LISTENING -> 0.40f
            OrbState.PROCESSING -> 0.15f
            OrbState.SPEAKING -> 0.30f
        },
        animationSpec = tween(600), label = "blobAmp"
    )

    // === Glow ===
    val baseGlow by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glow"
    )
    val glowIntensity by animateFloatAsState(
        targetValue = when (orbState) {
            OrbState.IDLE -> 0.25f
            OrbState.LISTENING -> 0.55f
            OrbState.PROCESSING -> 0.15f
            OrbState.SPEAKING -> 0.40f
        },
        animationSpec = tween(600), label = "glowI"
    )

    Box(
        modifier = modifier
            .scale(breathScale)
            .shadow(
                elevation = 30.dp,
                shape = CircleShape,
                spotColor = color1.copy(alpha = 0.12f + baseGlow * glowIntensity)
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            val c = center

            // Background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1E1E3A), Color(0xFF0A0A1A)),
                    center = c, radius = r
                )
            )

            // Blob 1
            val rad1 = angle1 * PI.toFloat() / 180f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color1.copy(alpha = 0.7f), color1.copy(alpha = 0f)),
                    center = c + Offset(
                        cos(rad1) * r * blobAmplitude,
                        sin(rad1) * r * blobAmplitude * 0.8f
                    ),
                    radius = r * 0.7f
                )
            )

            // Blob 2
            val rad2 = angle2 * PI.toFloat() / 180f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color2.copy(alpha = 0.55f), color2.copy(alpha = 0f)),
                    center = c + Offset(
                        cos(rad2) * r * blobAmplitude * 0.9f,
                        sin(rad2) * r * blobAmplitude
                    ),
                    radius = r * 0.65f
                )
            )

            // Blob 3
            val rad3 = angle3 * PI.toFloat() / 180f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color3.copy(alpha = 0.45f), color3.copy(alpha = 0f)),
                    center = c + Offset(
                        cos(rad3) * r * blobAmplitude * 0.7f,
                        sin(rad3) * r * blobAmplitude * 1.1f
                    ),
                    radius = r * 0.55f
                )
            )

            // Glass highlight (top-left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(c.x - r * 0.25f, c.y - r * 0.3f),
                    radius = r * 0.35f
                )
            )

            // Rim glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, color1.copy(alpha = 0.12f)),
                    center = c, radius = r
                )
            )
        }

        // State icon (subtle overlay)
        AnimatedContent(
            targetState = orbState,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            label = "icon"
        ) { state ->
            when (state) {
                OrbState.IDLE -> Icon(
                    Icons.Default.Mic,
                    contentDescription = "Говорити",
                    tint = Color.White.copy(alpha = 0.40f),
                    modifier = Modifier.size(36.dp)
                )
                OrbState.LISTENING -> Icon(
                    Icons.Default.Stop,
                    contentDescription = "Зупинити",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )
                OrbState.PROCESSING -> CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.5.dp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                OrbState.SPEAKING -> Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Говорю",
                    tint = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

// ===== TRANSCRIPT PANEL (collapsible message panel) =====

@Composable
private fun TranscriptPanel(
    messages: List<Message>,
    expanded: Boolean,
    onToggle: () -> Unit,
    isSending: Boolean,
    isTtsSpeaking: Boolean,
    speakingMessageId: String?,
    onSpeakMessage: (Message) -> Unit,
    quickActions: List<String>,
    onQuickAction: (String) -> Unit,
    activeSimulation: SimulationScenario?,
    simulationStep: Int,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val listState = rememberLazyListState()
    val panelShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    // Auto-scroll when expanded and messages change
    LaunchedEffect(messages.size, expanded) {
        if (expanded && messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(panelShape)
            .background(Color(0xE6101020), panelShape)
            .border(1.dp, Color.White.copy(alpha = 0.08f), panelShape)
    ) {
        // Handle bar (always visible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onToggle()
                }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Handle pill
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.20f))
            )

            Spacer(Modifier.width(12.dp))

            // Preview or count
            if (!expanded) {
                val lastMessage = messages.lastOrNull()
                if (lastMessage != null) {
                    val prefix = if (lastMessage.role == MessageRole.ASSISTANT) "AI: " else ""
                    Text(
                        text = "$prefix${lastMessage.content}",
                        style = TextStyle(fontSize = 13.sp),
                        color = Color.White.copy(alpha = 0.50f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = "Бесіда (${messages.size})",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    color = Color.White.copy(alpha = 0.60f)
                )
                Spacer(Modifier.weight(1f))
            }

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown
                else Icons.Default.KeyboardArrowUp,
                contentDescription = if (expanded) "Згорнути" else "Розгорнути",
                tint = Color.White.copy(alpha = 0.40f),
                modifier = Modifier.size(20.dp)
            )
        }

        // Expanded content
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Simulation progress
                if (activeSimulation != null) {
                    item {
                        SimulationProgressBar(
                            currentStep = simulationStep + 1,
                            totalSteps = activeSimulation.steps.size
                        )
                    }
                }

                // Messages
                items(messages, key = { it.id }) { message ->
                    when (message.role) {
                        MessageRole.USER -> UserMessageBubble(
                            message = message.content,
                            timestamp = message.timestamp
                        )
                        MessageRole.ASSISTANT -> AssistantMessageBubble(
                            message = message.content,
                            timestamp = message.timestamp,
                            isSpeakingThis = isTtsSpeaking && speakingMessageId == message.id,
                            onSpeakClick = { onSpeakMessage(message) }
                        )
                        MessageRole.SYSTEM -> { /* skip */ }
                    }
                }

                // Typing indicator
                if (isSending) {
                    item { TypingIndicator() }
                }

                // Quick actions
                if (quickActions.isNotEmpty() && activeSimulation == null) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            itemsIndexed(quickActions) { index, action ->
                                QuickActionChip(
                                    text = action,
                                    onClick = { onQuickAction(action) },
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
}

// ===== COMPACT INPUT ROW =====

@Composable
private fun CompactInputRow(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val hasText = text.isNotBlank()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Text field
        val fieldShape = RoundedCornerShape(20.dp)
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 36.dp)
                .clip(fieldShape)
                .background(Color.White.copy(alpha = 0.06f), fieldShape)
                .border(1.dp, Color.White.copy(alpha = 0.08f), fieldShape)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (text.isEmpty()) {
                Text(
                    "Написати...",
                    color = Color.White.copy(alpha = 0.25f),
                    style = TextStyle(fontSize = 13.sp)
                )
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                enabled = enabled,
                maxLines = 3,
                textStyle = TextStyle(
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 13.sp
                ),
                cursorBrush = SolidColor(PrimaryColors.light),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Send button
        AnimatedVisibility(
            visible = hasText,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6))
                        )
                    )
                    .clickable(enabled = enabled && hasText) {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onSendClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Надіслати",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
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
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(0.dp))
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
                    onDismissRequest = { onShowMenuChange(false) },
                    modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp))
                ) {
                    if (hasMessages) {
                        DropdownMenuItem(
                            text = { Text("Очистити", color = Color(0xFF1F2937)) },
                            onClick = {
                                onShowMenuChange(false)
                                onClearConversation()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFF6B7280))
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Перезапустити", color = Color(0xFF1F2937)) },
                        onClick = {
                            onShowMenuChange(false)
                            onClearConversation()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF6B7280))
                        }
                    )
                }
            }
        }
    }
}

// ===== SUGGESTION GRID =====

@Composable
private fun SuggestionGrid(
    templates: List<ConversationTemplate>,
    onTemplateClick: (ConversationTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (rowIndex in templates.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionCard(
                    template = templates[rowIndex],
                    onClick = { onTemplateClick(templates[rowIndex]) },
                    modifier = Modifier.weight(1f)
                )
                if (rowIndex + 1 < templates.size) {
                    SuggestionCard(
                        template = templates[rowIndex + 1],
                        onClick = { onTemplateClick(templates[rowIndex + 1]) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
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
        Text(text = "\u26A0\uFE0F", fontSize = 14.sp)
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

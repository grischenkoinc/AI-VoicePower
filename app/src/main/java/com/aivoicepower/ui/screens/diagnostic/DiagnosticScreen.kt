package com.aivoicepower.ui.screens.diagnostic

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DiagnosticTask(
    val number: Int,
    val title: String,
    val subtitle: String,
    val icon: String,
    val prompt: Any, // String or AnnotatedString
    val tip: String,
    val isScrollable: Boolean = false,
    val hasEmotions: Boolean = false,
    val isFreeSpeech: Boolean = false
)

@Composable
fun DiagnosticScreen(
    onComplete: (List<String>) -> Unit, // Передаємо список шляхів до записів
    modifier: Modifier = Modifier,
    viewModel: DiagnosticViewModel = hiltViewModel()
) {
    val audioRecorder = viewModel.audioRecorder
    val audioPlayer = viewModel.audioPlayer
    val isPlaying by audioPlayer.isPlaying.collectAsState()

    var currentTask by remember { mutableIntStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var showRecordingDialog by remember { mutableStateOf(false) }
    var currentRecordings by remember { mutableStateOf(mutableMapOf<Int, String>()) }
    var recordingTime by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()
    val maxRecordingTime = 45 // секунд

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Start recording
            scope.launch {
                val filePath = audioRecorder.startRecording()
                isRecording = true
                recordingTime = 0
            }
        }
    }

    val tasks = remember {
        listOf(
            DiagnosticTask(
                number = 1,
                title = "Читання",
                subtitle = "Прочитайте текст природно",
                icon = "📖",
                prompt = "Ваш голос — це потужний інструмент впливу. Коли ви говорите впевнено та чітко, люди слухають вас уважніше. Розвивайте свої навички публічних виступів, і ви зможете надихати інших своїми ідеями!",
                tip = "Читайте спокійно, ніби розмовляєте з другом",
                isScrollable = true
            ),
            DiagnosticTask(
                number = 2,
                title = "Дикція",
                subtitle = "Чітко вимовте скоромовку",
                icon = "🎯",
                prompt = "Кричав Архип, Архип охрип. Не треба Архипу кричати до хрипу.",
                tip = "Не поспішайте, вимовляйте кожен звук чітко"
            ),
            DiagnosticTask(
                number = 3,
                title = "Емоції",
                subtitle = "Читайте з відповідними емоціями",
                icon = "🎭",
                prompt = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF0D9668), fontWeight = FontWeight.Bold)) {
                        append("Сьогодні чудовий день! Я радий бути тут і ділитися своїми думками. Кожна мить наповнена можливостями та новими відкриттями!")
                    }
                    append(" ")
                    withStyle(SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)) {
                        append("Іноді буває важко, і це абсолютно нормально. Але я не здаюсь і продовжую йти вперед, навіть коли здається складно. Кожен крок — це досвід.")
                    }
                    append(" ")
                    withStyle(SpanStyle(color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)) {
                        append("Я впевнений у собі! Я знаю свої сильні сторони і вірю у власні можливості. Я досягну всіх своїх цілей, бо маю чітке бачення!")
                    }
                },
                tip = "Передайте емоції через інтонацію голосу",
                hasEmotions = true,
                isScrollable = true
            ),
            DiagnosticTask(
                number = 4,
                title = "Вільна мова",
                subtitle = "Розкажіть про себе 30-45 сек",
                icon = "💬",
                prompt = "Розкажіть, чому ви хочете покращити своє мовлення? Які у вас цілі? Що ви сподіваєтесь досягти завдяки тренуванням?",
                tip = "Говоріть природно, 30-45 секунд",
                isFreeSpeech = true
            )
        )
    }

    // Timer for free speech
    LaunchedEffect(isRecording, currentTask) {
        if (isRecording && tasks[currentTask].isFreeSpeech) {
            while (isRecording && recordingTime < maxRecordingTime) {
                delay(1000)
                recordingTime++
            }
            if (recordingTime >= maxRecordingTime) {
                // Auto stop after 45 seconds
                val filePath = audioRecorder.stopRecording()
                isRecording = false
                if (filePath != null) {
                    currentRecordings[currentTask] = filePath
                    showRecordingDialog = true
                }
                recordingTime = 0
            }
        }
    }

    val currentTaskData = tasks[currentTask]
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ProgressDots(current = currentTask, total = tasks.size)

                Text(
                    text = "Діагностика",
                    style = AppTypography.displayLarge,
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp
                )
            }

            TaskCardNew(
                task = currentTaskData,
                isRecording = isRecording,
                hasRecording = currentRecordings[currentTask] != null,
                recordingProgress = if (currentTaskData.isFreeSpeech)
                    recordingTime.toFloat() / maxRecordingTime
                else 0f,
                onRecordClick = {
                    if (isRecording) {
                        // Stop recording
                        val filePath = audioRecorder.stopRecording()
                        isRecording = false
                        if (filePath != null) {
                            currentRecordings[currentTask] = filePath
                            showRecordingDialog = true
                        }
                        recordingTime = 0
                    } else {
                        // Request permission and start recording
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        } else {
                            scope.launch {
                                val filePath = audioRecorder.startRecording()
                                isRecording = true
                                recordingTime = 0
                            }
                        }
                    }
                }
            )

            // Navigation (тільки Назад, без Пропустити)
            if (currentTask > 0) {
                SecondaryButton(
                    text = "Назад",
                    onClick = {
                        currentTask--
                        isRecording = false
                        showRecordingDialog = false
                        audioPlayer.stop()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }

        // Recording Dialog (по центру, не dismissable)
        if (showRecordingDialog) {
            val audioPath = currentRecordings[currentTask] ?: ""
            RecordingDialog(
                taskNumber = currentTask + 1,
                audioPath = audioPath,
                audioDuration = audioPlayer.getDuration(audioPath),
                isPlaying = isPlaying,
                isLastTask = currentTask == tasks.size - 1,
                onListen = {
                    if (isPlaying) {
                        audioPlayer.pause()
                    } else {
                        audioPlayer.play(audioPath) {
                            // Playback completed - do nothing
                        }
                    }
                },
                onReRecord = {
                    currentRecordings.remove(currentTask)
                    audioPlayer.stop()
                    showRecordingDialog = false

                    // Request permission and start new recording
                    scope.launch {
                        delay(200)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        } else {
                            val filePath = audioRecorder.startRecording()
                            isRecording = true
                            recordingTime = 0
                        }
                    }
                },
                onNext = {
                    audioPlayer.stop()
                    showRecordingDialog = false

                    if (currentTask < tasks.size - 1) {
                        currentTask++
                    } else {
                        // All tasks completed - pass recording paths
                        onComplete(currentRecordings.values.toList())
                    }
                }
            )
        }
    }
}

@Composable
private fun RecordingDialog(
    taskNumber: Int,
    audioPath: String,
    audioDuration: Int,
    isPlaying: Boolean,
    isLastTask: Boolean,
    onListen: () -> Unit,
    onReRecord: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { /* Non-dismissable */ }) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 32.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .background(Color.White, RoundedCornerShape(32.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Success icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✓", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
            }

            Text(
                text = "Запис збережено!",
                style = AppTypography.titleLarge,
                color = TextColors.onLightPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // Audio file visual
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🎵", fontSize = 32.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Завдання $taskNumber",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatDuration(audioDuration),
                        style = AppTypography.bodySmall,
                        color = TextColors.onLightSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            // Actions
            DialogActionButton(
                icon = if (isPlaying) "⏸" else "🔊",
                text = if (isPlaying) "Зупинити" else "Прослухати",
                onClick = onListen
            )

            DialogActionButton(
                icon = "🔄",
                text = "Перезаписати",
                onClick = onReRecord
            )

            // Next button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .scaleOnPress()
                    .clickable(onClick = onNext),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isLastTask) "Завершити діагностику" else "Далі →",
                    style = AppTypography.titleMedium,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun DialogActionButton(
    icon: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFFF8F9FA), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
            .scaleOnPress()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 22.sp)
        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TaskCardNew(
    task: DiagnosticTask,
    isRecording: Boolean,
    hasRecording: Boolean,
    recordingProgress: Float,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .background(Color.White, RoundedCornerShape(32.dp))
            .then(
                if (isRecording) {
                    Modifier.border(
                        width = 4.dp,
                        color = Color(0xFF667EEA).copy(alpha = borderAlpha),
                        shape = RoundedCornerShape(32.dp)
                    )
                } else Modifier
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ),
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = task.icon, fontSize = 40.sp)
        }

        // Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Завдання ${task.number}/4",
                style = AppTypography.labelMedium,
                color = Color(0xFF667EEA),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = task.title,
                style = AppTypography.displayLarge,
                color = TextColors.onLightPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.8).sp
            )
            Text(
                text = task.subtitle,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        // Prompt
        if (task.hasEmotions) {
            EmotionalTextPrompt()
        } else {
            val promptScrollState = rememberScrollState()
            val canScroll = task.isScrollable && promptScrollState.maxValue > 0
            val showScrollIndicator = canScroll && !promptScrollState.canScrollForward.not()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = 120.dp,
                            max = if (task.number == 1) 240.dp else 180.dp
                        )
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(20.dp))
                        .border(
                            width = 1.dp,
                            color = if (task.isScrollable) Color(0xFFE5E7EB) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    when (val prompt = task.prompt) {
                        is String -> Text(
                            text = prompt,
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp,
                            textAlign = if (task.isScrollable) TextAlign.Start else TextAlign.Center,
                            modifier = if (task.isScrollable) Modifier.verticalScroll(promptScrollState) else Modifier
                        )
                        else -> Text(
                            text = prompt as androidx.compose.ui.text.AnnotatedString,
                            style = AppTypography.bodyMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start,
                            modifier = if (task.isScrollable) Modifier.verticalScroll(promptScrollState) else Modifier
                        )
                    }
                }

                // Scroll indicator at bottom
                if (task.isScrollable) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "⌄",
                            color = Color(0xFF667EEA).copy(alpha = if (showScrollIndicator) 0.6f else 0f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Tip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFFBEB), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "💡", fontSize = 24.sp)
            Text(
                text = task.tip,
                style = AppTypography.bodySmall,
                color = Color(0xFF92400E),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // Record Button with wave rings and timer
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Circular timer for free speech
            if (task.isFreeSpeech && isRecording) {
                CircularTimer(progress = recordingProgress)
            }

            // Wave rings when recording
            if (isRecording && !task.isFreeSpeech) {
                repeat(3) { index ->
                    WaveRing(
                        delay = index * 600,
                        color = Color(0xFF667EEA)
                    )
                }
            }

            // Main button (НЕ червоний!)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2)) // Завжди фіолетовий!
                        ),
                        CircleShape
                    )
                    .scaleOnPress(pressedScale = 0.92f)
                    .clickable(onClick = onRecordClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRecording) "⏹" else if (hasRecording) "✓" else "🎤",
                    fontSize = 48.sp
                )
            }
        }

        // Status
        if (isRecording) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .pulseAnimation()
                        .background(Color(0xFFEF4444), CircleShape)
                )
                Text(
                    text = if (task.isFreeSpeech) "Запис... ${recordingProgress.times(45).toInt()}с" else "Запис...",
                    style = AppTypography.bodyMedium,
                    color = Color(0xFFEF4444),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CircularTimer(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(160.dp)) {
        val strokeWidth = 6.dp.toPx()
        drawArc(
            color = Color(0xFF667EEA),
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun EmotionalTextPrompt(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 270.dp) // ВИПРАВЛЕНО: було 180dp, +50%
            .background(Color(0xFFF8F9FA), RoundedCornerShape(20.dp))
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Читайте текст з відповідними емоціями:",
            style = AppTypography.labelSmall,
            color = TextColors.onLightSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LegendItem(color = Color(0xFF0D9668), label = "Радість") // ТЕМНІШИЙ зелений
            LegendItem(color = Color(0xFF6366F1), label = "Сум")
            LegendItem(color = Color(0xFFF59E0B), label = "Впевненість")
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFF0D9668), fontWeight = FontWeight.Bold)) { // ТЕМНІШИЙ
                    append("Сьогодні чудовий день! Кожна мить наповнена можливостями та новими відкриттями!")
                }
                append(" ")
                withStyle(SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)) {
                    append("Іноді буває важко, і це абсолютно нормально. Але я не здаюсь і продовжую йти вперед, навіть коли здається складно. ")
                }
                append(" ")
                withStyle(SpanStyle(color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)) {
                    append("Я знаю свої сильні сторони і вірю у власні можливості. Я досягну всіх своїх цілей, бо маю чітке бачення!")
                }
            },
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = TextColors.onLightSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProgressDots(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index <= current)
                            Color.White
                        else
                            Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun WaveRing(
    delay: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .border(
                width = 3.dp,
                color = color.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// Helper functions
private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) {
        String.format("%d:%02d", minutes, secs)
    } else {
        String.format("0:%02d", secs)
    }
}


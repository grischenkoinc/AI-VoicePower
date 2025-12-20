package com.aivoicepower.ui.screens.courses

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicepower.ui.theme.*
import com.aivoicepower.utils.audio.AudioRecorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onNavigateBack: () -> Unit,
    onExerciseComplete: (String) -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val recordingState by viewModel.recordingState.collectAsState()

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }

    var hasAudioPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is LessonUiState.Success) {
                        Text((uiState as LessonUiState.Success).lesson.title)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is LessonUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LessonUiState.Success -> {
                if (state.currentExercise != null) {
                    ExerciseContent(
                        exercise = state.currentExercise,
                        currentStepIndex = state.currentStepIndex,
                        recordingState = recordingState,
                        hasPermission = hasAudioPermission,
                        onStartRecording = {
                            audioRecorder.startRecording()
                            viewModel.startRecording()
                        },
                        onStopRecording = {
                            val file = audioRecorder.stopRecording()
                            viewModel.stopRecording(file)
                        },
                        onRetry = { viewModel.resetRecording() },
                        onNextExercise = { viewModel.moveToNextExercise() },
                        onNextStep = { viewModel.nextStep() },
                        onPreviousStep = { viewModel.previousStep() },
                        onSkipToRecording = { viewModel.skipToRecording() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            is LessonUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = Error)
                }
            }
        }
    }
}

@Composable
private fun ExerciseContent(
    exercise: com.aivoicepower.domain.model.course.Exercise,
    currentStepIndex: Int,
    recordingState: RecordingState,
    hasPermission: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onRetry: () -> Unit,
    onNextExercise: () -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onSkipToRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showSteps = exercise.steps.isNotEmpty() && currentStepIndex < exercise.steps.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Exercise Header with Difficulty Badge
        ExerciseHeader(exercise = exercise)

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Indicator for steps
        if (exercise.steps.isNotEmpty()) {
            StepProgressIndicator(
                currentStep = currentStepIndex,
                totalSteps = exercise.steps.size
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Main Content: Steps or Recording
        if (showSteps) {
            // Show current step
            val currentStep = exercise.steps[currentStepIndex]
            StepContent(
                step = currentStep,
                stepNumber = currentStepIndex + 1,
                totalSteps = exercise.steps.size,
                exampleText = exercise.exampleText
            )

            Spacer(modifier = Modifier.weight(1f))

            // Step Navigation
            StepNavigation(
                currentStep = currentStepIndex,
                totalSteps = exercise.steps.size,
                onPrevious = onPreviousStep,
                onNext = onNextStep,
                onSkip = onSkipToRecording
            )
        } else {
            // Show recording interface
            when (recordingState) {
                is RecordingState.Idle -> {
                    // Show example text if available
                    if (!exercise.exampleText.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Текст для вправи:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = exercise.exampleText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = Primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    RecordButton(
                        isRecording = false,
                        hasPermission = hasPermission,
                        onClick = onStartRecording
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Натисніть, щоб почати запис",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                is RecordingState.Recording -> {
                    // Show example text during recording
                    if (!exercise.exampleText.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = RecordingActive.copy(alpha = 0.15f)),
                            border = BorderStroke(2.dp, RecordingActive)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = RecordingActive,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Йде запис - читайте текст:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = RecordingActive,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = exercise.exampleText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = RecordingActive
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    RecordButton(
                        isRecording = true,
                        hasPermission = hasPermission,
                        onClick = onStopRecording
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Запис... Натисніть, щоб зупинити",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RecordingActive,
                        fontWeight = FontWeight.Bold
                    )
                }
                is RecordingState.Processing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "AI аналізує вашу промову...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                is RecordingState.Completed -> {
                    AnalysisResults(
                        analysis = recordingState.analysis,
                        onRetry = onRetry,
                        onNext = onNextExercise
                    )
                }
                is RecordingState.Error -> {
                    Text(
                        text = recordingState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onRetry) {
                        Text("Спробувати знову")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun RecordButton(
    isRecording: Boolean,
    hasPermission: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isRecording) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = { if (hasPermission) onClick() },
        modifier = Modifier
            .size(120.dp)
            .scale(if (isRecording) scale else 1f),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isRecording) RecordingActive else Primary
        ),
        enabled = hasPermission
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = if (isRecording) "Зупинити" else "Записати",
            modifier = Modifier.size(48.dp),
            tint = Color.White
        )
    }
}

@Composable
private fun AnalysisResults(
    analysis: com.aivoicepower.domain.model.analysis.VoiceAnalysis,
    onRetry: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Score Circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(8.dp, getScoreColor(analysis.overallScore), CircleShape)
                .clip(CircleShape)
                .background(getScoreColor(analysis.overallScore).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${analysis.overallScore.toInt()}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = getScoreColor(analysis.overallScore)
                )
                Text(
                    text = "балів",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feedback Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Фідбек від AI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = analysis.feedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Strengths
        if (analysis.strengths.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Success,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Сильні сторони",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Success
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    analysis.strengths.forEach { strength ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(text = "✓ ", style = MaterialTheme.typography.bodyMedium, color = Success)
                            Text(
                                text = strength,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Recommendations
        if (analysis.recommendations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Рекомендації",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    analysis.recommendations.forEach { recommendation ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(text = "• ", style = MaterialTheme.typography.bodyMedium, color = Primary)
                            Text(
                                text = recommendation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Повторити")
            }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f)
            ) {
                Text("Далі")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

// New Components for Step-by-Step UI

@Composable
private fun ExerciseHeader(exercise: com.aivoicepower.domain.model.course.Exercise) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
                DifficultyBadge(difficulty = exercise.difficulty)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercise.instruction,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            if (exercise.repetitions > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Повторити ${exercise.repetitions} разів",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: com.aivoicepower.domain.model.course.Difficulty) {
    val (text, color) = when (difficulty) {
        com.aivoicepower.domain.model.course.Difficulty.BEGINNER -> "Початковий" to Success
        com.aivoicepower.domain.model.course.Difficulty.INTERMEDIATE -> "Середній" to Warning
        com.aivoicepower.domain.model.course.Difficulty.ADVANCED -> "Просунутий" to Error
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StepProgressIndicator(currentStep: Int, totalSteps: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Крок ${currentStep + 1} з $totalSteps",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Text(
                text = "${((currentStep + 1) * 100 / totalSteps)}%",
                style = MaterialTheme.typography.labelMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = (currentStep + 1).toFloat() / totalSteps,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Primary,
            trackColor = Primary.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun StepContent(
    step: com.aivoicepower.domain.model.course.ExerciseStep,
    stepNumber: Int,
    totalSteps: Int,
    exampleText: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Step Title
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Primary)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                if (step.durationSeconds > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${step.durationSeconds} сек",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step Instruction
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = step.instruction,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Tips
        if (step.tips.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Success,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Поради:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Success
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    step.tips.forEach { tip ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "• ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Success
                            )
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Example Text (if on last step or no more steps)
        if (!exampleText.isNullOrEmpty() && stepNumber == totalSteps) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Текст для вправи:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = exampleText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun StepNavigation(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Previous Button
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f),
                enabled = currentStep > 0
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Назад")
            }

            // Next/Finish Button
            Button(
                onClick = if (currentStep < totalSteps - 1) onNext else onSkip,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (currentStep < totalSteps - 1) "Далі" else "До запису")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (currentStep < totalSteps - 1) Icons.Default.ArrowForward else Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Skip to recording button
        if (currentStep < totalSteps - 1) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onSkip) {
                Text(
                    text = "Пропустити інструкції →",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

private fun getScoreColor(score: Float): Color {
    return when {
        score >= 80 -> Success
        score >= 60 -> Warning
        else -> Error
    }
}

package com.aivoicepower.ui.screens.lesson

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
import com.aivoicepower.domain.model.course.TheoryContent
import com.aivoicepower.domain.model.exercise.ExerciseContent
import com.aivoicepower.domain.model.exercise.ExerciseType
import com.aivoicepower.ui.theme.*
import com.aivoicepower.utils.AudioRecorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    onNavigateBack: () -> Unit,
    onExerciseComplete: (String) -> Unit = {},
    viewModel: LessonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val recordingState by viewModel.recordingState.collectAsState()

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }

    var hasAudioPermission by remember { mutableStateOf(false) }
    var showTheory by remember { mutableStateOf(true) }

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
                when {
                    state.isCompleted -> {
                        LessonCompletedSection(
                            lessonTitle = state.lesson.title,
                            exercisesCount = state.lesson.exercises.size,
                            nextLessonId = state.nextLessonId,
                            nextLessonTitle = state.nextLessonTitle,
                            isLastLessonInCourse = state.isLastLessonInCourse,
                            onNextLesson = { nextId -> viewModel.loadNextLesson(nextId) },
                            onFinish = onNavigateBack,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    showTheory && state.lesson.theory != null -> {
                        TheorySection(
                            theory = state.lesson.theory!!,
                            onContinue = { showTheory = false },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    state.currentExercise != null -> {
                        ExerciseContent(
                            exercise = state.currentExercise,
                            currentStepIndex = state.currentStepIndex,
                            currentExerciseIndex = state.currentExerciseIndex,
                            totalExercises = state.lesson.exercises.size,
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
                            onPreviousExercise = { viewModel.moveToPreviousExercise() },
                            onNextStep = { viewModel.nextStep() },
                            onPreviousStep = { viewModel.previousStep() },
                            onSkipToRecording = { viewModel.skipToRecording() },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
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
    exercise: com.aivoicepower.domain.model.exercise.Exercise,
    currentStepIndex: Int,
    currentExerciseIndex: Int,
    totalExercises: Int,
    recordingState: RecordingState,
    hasPermission: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onRetry: () -> Unit,
    onNextExercise: () -> Unit,
    onPreviousExercise: () -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onSkipToRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showSteps = exercise.steps.isNotEmpty() && currentStepIndex < exercise.steps.size
    val isArticulation = exercise.type == ExerciseType.ARTICULATION

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Exercise Header with Difficulty Badge
        ExerciseHeader(exercise = exercise)

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Indicator for steps (only for non-articulation with steps)
        if (exercise.steps.isNotEmpty() && !isArticulation) {
            StepProgressIndicator(
                currentStep = currentStepIndex,
                totalSteps = exercise.steps.size
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // For articulation exercises - "Повернутись" + "Виконано" UI
        if (isArticulation) {
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Кнопка "Повернутись" - показувати якщо не перша вправа
                if (currentExerciseIndex > 0) {
                    OutlinedButton(
                        onClick = onPreviousExercise,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Повернутись",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Кнопка "Виконано"
                Button(
                    onClick = onNextExercise,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Виконано",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
        // Main Content: Steps or Recording
        else if (showSteps) {
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
                    // Get text to display - from content for TongueTwister, or from exampleText
                    val displayText = when {
                        exercise.type == ExerciseType.TONGUE_TWISTER && exercise.content is ExerciseContent.TongueTwister -> {
                            (exercise.content as ExerciseContent.TongueTwister).text
                        }
                        !exercise.exampleText.isNullOrEmpty() -> exercise.exampleText
                        else -> null
                    }

                    val targetSounds = if (exercise.type == ExerciseType.TONGUE_TWISTER && exercise.content is ExerciseContent.TongueTwister) {
                        (exercise.content as ExerciseContent.TongueTwister).targetSounds
                    } else null

                    // Show text if available
                    if (!displayText.isNullOrEmpty()) {
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
                                    text = if (exercise.type == ExerciseType.TONGUE_TWISTER) "Скоромовка:" else "Текст для вправи:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = displayText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = Primary
                                )
                                // Show target sounds for tongue twisters
                                if (!targetSounds.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Цільові звуки: ${targetSounds.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
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

                    // Кнопки навігації
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Кнопка "Повернутись" - якщо не перша вправа
                        if (currentExerciseIndex > 0) {
                            OutlinedButton(
                                onClick = onPreviousExercise,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Повернутись")
                            }
                        }

                        // Кнопка "Пропустити" для скоромовок
                        if (exercise.type == ExerciseType.TONGUE_TWISTER) {
                            OutlinedButton(
                                onClick = onNextExercise,
                                modifier = if (currentExerciseIndex > 0) Modifier.weight(1f) else Modifier
                            ) {
                                Text("Пропустити")
                            }
                        }
                    }
                }
                is RecordingState.Recording -> {
                    // Get text to display during recording
                    val recordingDisplayText = when {
                        exercise.type == ExerciseType.TONGUE_TWISTER && exercise.content is ExerciseContent.TongueTwister -> {
                            (exercise.content as ExerciseContent.TongueTwister).text
                        }
                        !exercise.exampleText.isNullOrEmpty() -> exercise.exampleText
                        else -> null
                    }

                    if (!recordingDisplayText.isNullOrEmpty()) {
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
                                        text = if (exercise.type == ExerciseType.TONGUE_TWISTER) "Йде запис - читайте скоромовку:" else "Йде запис - читайте текст:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = RecordingActive,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = recordingDisplayText,
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
                        currentExerciseIndex = currentExerciseIndex,
                        totalExercises = totalExercises,
                        onRetry = onRetry,
                        onNext = onNextExercise,
                        onPrevious = onPreviousExercise
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
    analysis: com.aivoicepower.domain.model.VoiceAnalysis,
    currentExerciseIndex: Int,
    totalExercises: Int,
    onRetry: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val isFirstExercise = currentExerciseIndex == 0
    val isLastExercise = currentExerciseIndex >= totalExercises - 1

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
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Retry button
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Повторити запис")
            }

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isFirstExercise) {
                    OutlinedButton(
                        onClick = onPrevious,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Попередня")
                    }
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isLastExercise) "Завершити" else "Наступна")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        if (isLastExercise) Icons.Default.Check else Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

// New Components for Step-by-Step UI

@Composable
private fun ExerciseHeader(exercise: com.aivoicepower.domain.model.exercise.Exercise) {
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

@Composable
private fun TheorySection(
    theory: TheoryContent,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Теорія",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Основний текст теорії
        Text(
            text = theory.text,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поради
        if (theory.tips.isNotEmpty()) {
            Text(
                text = "Поради:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            theory.tips.forEach { tip ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("• ", style = MaterialTheme.typography.bodyMedium)
                    Text(tip, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Почати вправи")
        }
    }
}

@Composable
private fun LessonCompletedSection(
    lessonTitle: String,
    exercisesCount: Int,
    nextLessonId: String?,
    nextLessonTitle: String?,
    isLastLessonInCourse: Boolean,
    onNextLesson: (String) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goldColor = Color(0xFFFFD700)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Icon - різне для завершення уроку та курсу
        Box(
            modifier = Modifier
                .size(if (isLastLessonInCourse) 120.dp else 100.dp)
                .clip(CircleShape)
                .background(if (isLastLessonInCourse) goldColor.copy(alpha = 0.15f) else Success.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLastLessonInCourse) Icons.Default.Star else Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(if (isLastLessonInCourse) 70.dp else 60.dp),
                tint = if (isLastLessonInCourse) goldColor else Success
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isLastLessonInCourse) "Курс завершено!" else "Урок пройдено!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLastLessonInCourse) {
            Text(
                text = "Вітаємо! Ви успішно пройшли весь курс",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Мотиваційне повідомлення
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = goldColor.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "Ваша наполеглива праця принесла результат! Продовжуйте вдосконалювати свої навички.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = lessonTitle,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ви виконали $exercisesCount ${getExercisesWord(exercisesCount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка "До наступного уроку" - якщо є наступний урок
        if (!isLastLessonInCourse && nextLessonId != null && nextLessonTitle != null) {
            Button(
                onClick = { onNextLesson(nextLessonId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "До наступного уроку",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = nextLessonTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Кнопка "Повернутись до курсу" / "До списку курсів"
        OutlinedButton(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Повернутись до курсу")
        }
    }
}

private fun getExercisesWord(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "вправу"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "вправи"
        else -> "вправ"
    }
}

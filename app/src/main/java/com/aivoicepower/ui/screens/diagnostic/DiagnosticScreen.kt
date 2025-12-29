package com.aivoicepower.ui.screens.diagnostic

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.diagnostic.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticScreen(
    viewModel: DiagnosticViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Permission state
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            // Permission granted - start recording
            viewModel.onEvent(DiagnosticEvent.StartRecordingClicked)
        } else {
            // Permission denied - show error in dialog
            viewModel.onEvent(DiagnosticEvent.PermissionDenied)
        }
    }

    // Navigate when diagnostic is completed
    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onNavigateToHome()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Діагностика") },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрити"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Progress bar
            DiagnosticProgressBar(
                completedTasks = state.completedTasksCount,
                totalTasks = state.tasks.size
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Task list
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.tasks.forEachIndexed { index, task ->
                    DiagnosticTaskCard(
                        task = task,
                        onClick = {
                            viewModel.onEvent(DiagnosticEvent.TaskClicked(index))
                        }
                    )
                }
            }

            // Complete button (shown when all tasks are recorded and NOT analyzing)
            if (state.completedTasksCount == state.tasks.size && state.completedTasksCount > 0 && !state.isAnalyzing) {
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.onEvent(DiagnosticEvent.CompleteDiagnosticClicked)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Завершити діагностику →")
                }
            }
        }
    }

    // Dialogs
    if (state.showInstructionDialog && state.selectedTask != null) {
        DiagnosticInstructionDialog(
            task = state.selectedTask!!,
            error = state.error,
            onDismiss = {
                viewModel.onEvent(DiagnosticEvent.InstructionDialogDismissed)
            },
            onStartRecording = {
                // Check and request permission before starting recording
                if (hasAudioPermission) {
                    viewModel.onEvent(DiagnosticEvent.StartRecordingClicked)
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        )
    }

    if (state.isRecording && state.selectedTask != null) {
        DiagnosticRecordingDialog(
            recordingSeconds = state.recordingSeconds,
            maxDurationSeconds = state.selectedTask!!.durationSeconds,
            contentText = state.selectedTask!!.contentText,
            onStopRecording = {
                viewModel.onEvent(DiagnosticEvent.StopRecordingClicked)
            }
        )
    }

    if (state.showRecordingPreview) {
        DiagnosticRecordingPreviewDialog(
            recordingDurationSeconds = state.recordingSeconds,
            recordingPath = state.currentRecordingPath,
            onRetake = {
                viewModel.onEvent(DiagnosticEvent.RetakeRecordingClicked)
            },
            onSave = {
                viewModel.onEvent(DiagnosticEvent.SaveRecordingClicked)
            },
            onDismiss = {
                viewModel.onEvent(DiagnosticEvent.PreviewDialogDismissed)
            }
        )
    }

    // Analyzing dialog - blocks UI during analysis
    if (state.isAnalyzing) {
        DiagnosticAnalyzingDialog(
            currentTaskIndex = state.currentAnalyzingTaskIndex,
            totalTasks = state.tasks.size
        )
    }
}

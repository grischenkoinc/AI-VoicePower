package com.aivoicepower.ui.screens.diagnostic

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

            // Complete button (shown when all tasks are recorded)
            if (state.completedTasksCount == state.tasks.size && state.completedTasksCount > 0) {
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
            onDismiss = {
                viewModel.onEvent(DiagnosticEvent.InstructionDialogDismissed)
            },
            onStartRecording = {
                viewModel.onEvent(DiagnosticEvent.StartRecordingClicked)
            }
        )
    }

    if (state.isRecording && state.selectedTask != null) {
        DiagnosticRecordingDialog(
            recordingSeconds = state.recordingSeconds,
            maxDurationSeconds = state.selectedTask!!.durationSeconds,
            onStopRecording = {
                viewModel.onEvent(DiagnosticEvent.StopRecordingClicked)
            }
        )
    }

    if (state.showRecordingPreview) {
        DiagnosticRecordingPreviewDialog(
            recordingDurationSeconds = state.recordingSeconds,
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
}

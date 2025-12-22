package com.aivoicepower.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.results.components.AnalysisPlaceholderCard
import com.aivoicepower.ui.screens.results.components.RecordingInfoCard
import com.aivoicepower.ui.screens.results.components.ResultsActionsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    recordingId: String,
    viewModel: ResultsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результати") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(state.error!!)
                        Button(onClick = onNavigateBack) {
                            Text("Повернутися")
                        }
                    }
                }
            }

            state.recording != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Recording info
                    RecordingInfoCard(
                        recording = state.recording!!,
                        isPlaying = state.isPlaying,
                        onPlayClicked = {
                            if (state.isPlaying) {
                                viewModel.onEvent(ResultsEvent.StopPlaybackClicked)
                            } else {
                                viewModel.onEvent(ResultsEvent.PlayRecordingClicked)
                            }
                        }
                    )

                    // Analysis section
                    if (state.analysis?.isAnalyzed == true) {
                        AnalysisPlaceholderCard(
                            message = "AI-аналiз буде додано в Phase 6",
                            score = state.analysis?.overallScore
                        )
                    } else {
                        AnalysisPlaceholderCard(
                            message = "Аналiз буде доступний пiсля iнтеграцiї AI в Phase 6"
                        )
                    }

                    // Actions
                    ResultsActionsCard(
                        onRetry = {
                            viewModel.onEvent(ResultsEvent.RetryExerciseClicked)
                            onNavigateBack() // For now, just go back
                        },
                        onNext = {
                            viewModel.onEvent(ResultsEvent.NextExerciseClicked)
                            onNavigateBack() // For now, just go back
                        }
                    )
                }
            }
        }
    }
}

package com.aivoicepower.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordingHistoryScreen(
    viewModel: RecordingHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            RecordingHistoryHeader(onNavigateBack = onNavigateBack)

            // Filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RecordingFilter.entries) { filter ->
                    FilterChip(
                        selected = state.selectedFilter == filter,
                        onClick = {
                            viewModel.onEvent(RecordingHistoryEvent.FilterSelected(filter))
                        },
                        label = {
                            Text(
                                when (filter) {
                                    RecordingFilter.ALL -> "Всі"
                                    RecordingFilter.DIAGNOSTIC -> "Діагностика"
                                    RecordingFilter.COURSE -> "Курси"
                                    RecordingFilter.IMPROVISATION -> "Імпровізація"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF667EEA),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF667EEA)
                        )
                    )
                }
            }

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                state.recordings.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 20.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = Color.Black.copy(alpha = 0.18f),
                                    ambientColor = Color.Black.copy(alpha = 0.08f)
                                )
                                .background(Color.White, RoundedCornerShape(20.dp))
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Записи відсутні",
                                style = AppTypography.bodyLarge,
                                color = TextColors.onLightPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.recordings) { recording ->
                            RecordingListItem(
                                recording = recording,
                                isPlaying = state.playingRecordingId == recording.id,
                                onPlay = {
                                    viewModel.onEvent(RecordingHistoryEvent.PlayRecording(recording))
                                },
                                onStop = {
                                    viewModel.onEvent(RecordingHistoryEvent.StopPlayback)
                                },
                                onDelete = {
                                    viewModel.onEvent(RecordingHistoryEvent.RequestDeleteRecording(recording.id))
                                },
                                onViewResults = {
                                    onNavigateToResults(recording.id)
                                }
                            )
                        }

                        item {
                            BackButton(onClick = onNavigateBack)
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (state.showDeleteConfirmation) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { viewModel.onEvent(RecordingHistoryEvent.CancelDelete) },
                title = {
                    Text(
                        text = "Видалити запис?",
                        style = AppTypography.titleLarge,
                        color = TextColors.onLightPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Цю дію неможливо скасувати. Запис буде видалено назавжди.",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightSecondary,
                        fontSize = 15.sp
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.onEvent(RecordingHistoryEvent.ConfirmDelete) }
                    ) {
                        Text(
                            text = "Видалити",
                            color = Color(0xFFEF4444),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.onEvent(RecordingHistoryEvent.CancelDelete) }
                    ) {
                        Text(
                            text = "Скасувати",
                            color = Color(0xFF667EEA),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun RecordingHistoryHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Мої записи",
            style = AppTypography.labelMedium,
            color = TextColors.onDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Історія записів",
            style = AppTypography.displayLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.8).sp
        )
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "← Назад",
            style = AppTypography.bodyLarge,
            color = Color(0xFF667EEA),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecordingListItem(
    recording: RecordingEntity,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit,
    onViewResults: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.18f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable { onViewResults() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getRecordingTypeLabel(recording.type),
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDate(recording.createdAt),
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = formatDuration(recording.durationMs),
                style = AppTypography.bodySmall,
                color = Color(0xFF667EEA),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = if (isPlaying) onStop else onPlay) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Стоп" else "Грати",
                    tint = Color(0xFF667EEA)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Видалити",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

private fun getRecordingTypeLabel(type: String): String {
    return when (type) {
        "diagnostic" -> "Діагностика"
        "exercise" -> "Вправа"
        "improvisation" -> "Імпровізація"
        else -> "Запис"
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("uk"))
    return sdf.format(Date(timestamp))
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.screens.courses.ExerciseState
import com.aivoicepower.ui.screens.courses.LessonEvent

@Composable
fun ExercisePhaseContent(
    lesson: Lesson,
    currentExerciseIndex: Int,
    exerciseState: ExerciseState?,
    totalExercises: Int,
    isPlaying: Boolean,
    onEvent: (LessonEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (exerciseState == null) return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress indicator
        Text(
            text = "Вправа ${currentExerciseIndex + 1}/$totalExercises",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        LinearProgressIndicator(
            progress = (currentExerciseIndex + 1) / totalExercises.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        // Exercise card
        ExerciseCard(exerciseState = exerciseState)

        // Recording controls
        RecordingControls(
            exerciseState = exerciseState,
            isPlaying = isPlaying,
            onStartRecording = { onEvent(LessonEvent.StartRecordingClicked) },
            onStopRecording = { onEvent(LessonEvent.StopRecordingClicked) },
            onPlayRecording = { onEvent(LessonEvent.PlayRecordingClicked) },
            onStopPlayback = { onEvent(LessonEvent.StopPlaybackClicked) },
            onReRecord = { onEvent(LessonEvent.ReRecordClicked) },
            onComplete = { onEvent(LessonEvent.CompleteExerciseClicked) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (currentExerciseIndex > 0) {
                OutlinedButton(
                    onClick = { onEvent(LessonEvent.PreviousExerciseClicked) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Попередня")
                }
            }

            OutlinedButton(
                onClick = { onEvent(LessonEvent.SkipExerciseClicked) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Пропустити")
            }
        }
    }
}

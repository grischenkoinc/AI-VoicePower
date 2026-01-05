package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.exercise.ExerciseType
import com.aivoicepower.domain.model.exercise.toDisplayString
import com.aivoicepower.ui.screens.courses.ExerciseState

@Composable
fun ExerciseCard(
    exerciseState: ExerciseState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Exercise type
            Text(
                text = "${getExerciseEmoji(exerciseState.exercise.type)} ${exerciseState.exercise.type.toDisplayString()}",
                style = MaterialTheme.typography.titleLarge
            )

            // Title
            Text(
                text = exerciseState.exercise.title,
                style = MaterialTheme.typography.titleMedium
            )

            // Instruction
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Iнструкцiя:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = exerciseState.exercise.instruction,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Content display
            ExerciseContentDisplay(content = exerciseState.exercise.content)

            // Duration
            Text(
                text = "Рекомендований час: ${exerciseState.exercise.durationSeconds} сек",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getExerciseEmoji(type: ExerciseType): String {
    return when (type) {
        ExerciseType.ARTICULATION -> "[A]"
        ExerciseType.TONGUE_TWISTER -> "[TT]"
        ExerciseType.READING -> "[R]"
        ExerciseType.EMOTION_READING -> "[ER]"
        ExerciseType.FREE_SPEECH -> "[FS]"
        ExerciseType.RETELLING -> "[RT]"
        ExerciseType.DIALOGUE -> "[DL]"
        ExerciseType.PITCH -> "[P]"
        ExerciseType.QA -> "[QA]"
        ExerciseType.TONGUE_TWISTER_BATTLE -> "[TB]"
        ExerciseType.MINIMAL_PAIRS -> "[MP]"
        ExerciseType.CONTRAST_SOUNDS -> "[CS]"
        ExerciseType.SLOW_MOTION -> "[SM]"
        ExerciseType.BREATHING -> "[BR]"
    }
}

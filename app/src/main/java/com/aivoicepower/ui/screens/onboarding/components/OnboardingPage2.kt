package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.user.UserGoal

@Composable
fun OnboardingPage2(
    selectedGoal: UserGoal,
    onGoalSelected: (UserGoal) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Яка твоя головна ціль?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoalOption(
                    emoji = "🗣️",
                    title = "Говорити чіткіше",
                    goal = UserGoal.CLEAR_SPEECH,
                    isSelected = selectedGoal == UserGoal.CLEAR_SPEECH,
                    onSelect = { onGoalSelected(UserGoal.CLEAR_SPEECH) }
                )

                GoalOption(
                    emoji = "🎤",
                    title = "Впевнені публічні виступи",
                    goal = UserGoal.PUBLIC_SPEAKING,
                    isSelected = selectedGoal == UserGoal.PUBLIC_SPEAKING,
                    onSelect = { onGoalSelected(UserGoal.PUBLIC_SPEAKING) }
                )

                GoalOption(
                    emoji = "🎵",
                    title = "Покращити голос",
                    goal = UserGoal.BETTER_VOICE,
                    isSelected = selectedGoal == UserGoal.BETTER_VOICE,
                    onSelect = { onGoalSelected(UserGoal.BETTER_VOICE) }
                )

                GoalOption(
                    emoji = "💼",
                    title = "Навчитись переконувати",
                    goal = UserGoal.PERSUASION,
                    isSelected = selectedGoal == UserGoal.PERSUASION,
                    onSelect = { onGoalSelected(UserGoal.PERSUASION) }
                )

                GoalOption(
                    emoji = "🤝",
                    title = "Підготовка до співбесіди",
                    goal = UserGoal.INTERVIEW_PREP,
                    isSelected = selectedGoal == UserGoal.INTERVIEW_PREP,
                    onSelect = { onGoalSelected(UserGoal.INTERVIEW_PREP) }
                )

                GoalOption(
                    emoji = "📚",
                    title = "Загальний розвиток",
                    goal = UserGoal.GENERAL,
                    isSelected = selectedGoal == UserGoal.GENERAL,
                    onSelect = { onGoalSelected(UserGoal.GENERAL) }
                )
            }
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBackClick) {
                    Text("← Назад")
                }

                Button(
                    onClick = onNextClick,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Далі →")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PageIndicator(currentPage = 1, totalPages = 4)
        }
    }
}

@Composable
private fun GoalOption(
    emoji: String,
    title: String,
    goal: UserGoal,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

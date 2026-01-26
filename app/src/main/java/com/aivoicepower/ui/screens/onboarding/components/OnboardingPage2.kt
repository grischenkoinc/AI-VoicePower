package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.user.UserGoal
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun OnboardingPage2(
    selectedGoal: UserGoal,
    onGoalSelected: (UserGoal) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(top = 48.dp, bottom = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Text(
                text = "Яка твоя головна ціль?",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )

            // Goals Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoalOption(
                    emoji = "🗣️",
                    title = "Говорити чіткіше",
                    isSelected = selectedGoal == UserGoal.CLEAR_SPEECH,
                    onSelect = { onGoalSelected(UserGoal.CLEAR_SPEECH) }
                )

                GoalOption(
                    emoji = "🎤",
                    title = "Впевнені публічні виступи",
                    isSelected = selectedGoal == UserGoal.PUBLIC_SPEAKING,
                    onSelect = { onGoalSelected(UserGoal.PUBLIC_SPEAKING) }
                )

                GoalOption(
                    emoji = "🎵",
                    title = "Покращити голос",
                    isSelected = selectedGoal == UserGoal.BETTER_VOICE,
                    onSelect = { onGoalSelected(UserGoal.BETTER_VOICE) }
                )

                GoalOption(
                    emoji = "💼",
                    title = "Навчитись переконувати",
                    isSelected = selectedGoal == UserGoal.PERSUASION,
                    onSelect = { onGoalSelected(UserGoal.PERSUASION) }
                )

                GoalOption(
                    emoji = "🤝",
                    title = "Підготовка до співбесіди",
                    isSelected = selectedGoal == UserGoal.INTERVIEW_PREP,
                    onSelect = { onGoalSelected(UserGoal.INTERVIEW_PREP) }
                )

                GoalOption(
                    emoji = "📚",
                    title = "Загальний розвиток",
                    isSelected = selectedGoal == UserGoal.GENERAL,
                    onSelect = { onGoalSelected(UserGoal.GENERAL) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "← Назад",
                            style = AppTypography.titleMedium,
                            color = TextColors.onLightPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Next button
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF667EEA).copy(alpha = 0.4f))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onNextClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Далі →",
                            style = AppTypography.titleMedium,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                PageIndicator(currentPage = 1, totalPages = 4)
            }
        }
    }
}

@Composable
private fun GoalOption(
    emoji: String,
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0xFF667EEA).copy(alpha = 0.1f)
                else Color(0xFFF8F9FA)
            )
            .then(
                if (isSelected) Modifier.border(2.dp, Color(0xFF667EEA), RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable(onClick = onSelect)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = emoji, fontSize = 24.sp)

            Text(
                text = title,
                style = AppTypography.bodyMedium,
                color = if (isSelected) Color(0xFF667EEA) else TextColors.onLightPrimary,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

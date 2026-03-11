package com.aivoicepower.ui.screens.improvisation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.components.FocusCountdownOverlay
import com.aivoicepower.ui.screens.improvisation.components.AnalyzingScreen
import com.aivoicepower.ui.components.AnalysisResultsContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.aivoicepower.ui.screens.improvisation.components.OrbState
import com.aivoicepower.ui.screens.improvisation.components.VoiceExerciseScreen
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.components.PrimaryButton

@Composable
fun JobInterviewScreen(
    viewModel: JobInterviewViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    var backPressedTime by remember { mutableStateOf(0L) }
    var showFocus by remember { mutableStateOf(false) }
    var focusDone by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.ttsManager.stop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.isStarted) {
        if (state.isStarted && !focusDone) {
            showFocus = true
        }
    }

    BackHandler(enabled = state.isStarted && state.currentRound <= state.maxRounds) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            onNavigateBack()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, "Натисніть ще раз, щоб повернутись", Toast.LENGTH_SHORT).show()
        }
    }

    when {
        state.analysisResult != null -> {
            Box(modifier = Modifier.fillMaxSize()) {
                GradientBackground(content = {})
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnalysisResultsContent(
                        result = state.analysisResult!!,
                        onDismiss = { viewModel.onEvent(JobInterviewEvent.DismissAnalysis); onNavigateBack() },
                        dismissButtonText = "Готово"
                    )
                }
            }
        }

        state.isAnalyzing -> {
            AnalyzingScreen()
        }

        state.selectedProfession == null -> {
            ProfessionSelectionScreen(
                onProfessionSelected = { viewModel.onEvent(JobInterviewEvent.ProfessionSelected(it)) },
                onNavigateBack = onNavigateBack
            )
        }

        !state.isStarted -> {
            InterviewWelcomeScreen(
                profession = state.selectedProfession ?: "",
                hrName = state.hrName,
                companyName = state.companyName,
                onStart = { viewModel.onEvent(JobInterviewEvent.StartSimulation) },
                onNavigateBack = onNavigateBack
            )
        }

        state.isStarted && showFocus -> {
            FocusCountdownOverlay(
                exerciseName = "Спiвбесiда",
                topic = state.selectedProfession ?: "",
                onComplete = { showFocus = false; focusDone = true; viewModel.onEvent(JobInterviewEvent.CountdownComplete) }
            )
        }

        state.currentRound <= state.maxRounds -> {
            VoiceExerciseScreen(
                title = "Спiвбесiда",
                stepInfo = "Крок ${state.currentRound}/${state.maxRounds}",
                roleEmoji = "\uD83D\uDCBC",
                roleName = state.hrName.ifBlank { "HR Manager" },
                aiText = state.aiText,
                hint = state.hint,
                orbState = state.orbState,
                audioLevel = state.audioLevel,
                isRecording = state.isRecording,
                recordingSeconds = state.recordingSeconds,
                onRecordClick = { viewModel.onEvent(JobInterviewEvent.StartRecording) },
                onStopClick = { viewModel.onEvent(JobInterviewEvent.StopRecording) },
                onBackClick = onNavigateBack,
                errorMessage = state.error
            )
        }

        else -> {
            VoiceExerciseScreen(
                title = "Спiвбесiда",
                stepInfo = "Завершено",
                roleEmoji = "\uD83D\uDCBC",
                roleName = state.hrName.ifBlank { "HR Manager" },
                aiText = state.aiText.ifBlank { "Дякую за спiвбесiду! Ви чудово впоралися." },
                hint = null,
                orbState = state.orbState,
                onRecordClick = {},
                onBackClick = { viewModel.onEvent(JobInterviewEvent.FinishInterview); onNavigateBack() },
                onAnalyzeClick = { viewModel.onEvent(JobInterviewEvent.AnalyzeClicked) },
                onSkipClick = { viewModel.onEvent(JobInterviewEvent.SkipClicked); onNavigateBack() },
                errorMessage = state.error
            )
        }
    }

}

// ========================================================================
// Profession Selection Screen
// ========================================================================

private data class ProfessionOption(
    val title: String,
    val emoji: String
)

private val PROFESSIONS = listOf(
    ProfessionOption("Frontend Developer", "\uD83C\uDF10"),
    ProfessionOption("Backend Developer", "\u2699\uFE0F"),
    ProfessionOption("Product Manager", "\uD83D\uDCCA"),
    ProfessionOption("UI/UX Дизайнер", "\uD83C\uDFA8"),
    ProfessionOption("Маркетолог", "\uD83D\uDCE2"),
    ProfessionOption("Менеджер з продажу", "\uD83D\uDCB0"),
    ProfessionOption("Фінансовий аналітик", "\uD83D\uDCC8"),
    ProfessionOption("HR-менеджер", "\uD83E\uDD1D"),
    ProfessionOption("Вчитель/Викладач", "\uD83D\uDCDA"),
    ProfessionOption("Лікар/Медик", "\uD83E\uDE7A"),
    ProfessionOption("Юрист/Адвокат", "\u2696\uFE0F"),
    ProfessionOption("Журналіст", "\uD83D\uDCF0"),
    ProfessionOption("Бухгалтер", "\uD83D\uDCCB"),
    ProfessionOption("Data Scientist", "\uD83E\uDDEE"),
    ProfessionOption("DevOps Engineer", "\uD83D\uDE80"),
    ProfessionOption("Логіст", "\uD83D\uDE9A"),
    ProfessionOption("Архітектор", "\uD83C\uDFD7\uFE0F"),
    ProfessionOption("Психолог", "\uD83E\uDDE0")
)

@Composable
private fun ProfessionSelectionScreen(
    onProfessionSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var customProfession by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 20.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Симуляція",
                        style = AppTypography.labelMedium,
                        color = TextColors.onDarkSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "\uD83D\uDCBC Співбесіда",
                        style = AppTypography.displayLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp
                    )
                }

                // Back button
                Row(
                    modifier = Modifier
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        )
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onNavigateBack() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u2190",
                        fontSize = 24.sp,
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "\u041D\u0430\u0437\u0430\u0434",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Обери професію",
                style = AppTypography.titleMedium,
                color = TextColors.onDarkSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Profession grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(PROFESSIONS) { profession ->
                    ProfessionCard(
                        profession = profession,
                        onClick = { onProfessionSelected(profession.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom profession input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.12f))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Або введи свою професію",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = customProfession,
                    onValueChange = { customProfession = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Наприклад: Data Scientist",
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightSecondary.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextColors.onLightPrimary,
                        unfocusedTextColor = TextColors.onLightPrimary,
                        focusedBorderColor = Color(0xFF667EEA),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        cursorColor = Color(0xFF667EEA),
                        focusedContainerColor = Color(0xFFF9FAFB),
                        unfocusedContainerColor = Color(0xFFF9FAFB)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (customProfession.isNotBlank()) {
                                onProfessionSelected(customProfession.trim())
                            }
                        }
                    )
                )

                PrimaryButton(
                    text = "Далі",
                    onClick = {
                        if (customProfession.isNotBlank()) {
                            onProfessionSelected(customProfession.trim())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ProfessionCard(
    profession: ProfessionOption,
    onClick: () -> Unit
) {
    val view = LocalView.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .background(Color.White, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onClick() })
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = profession.emoji,
            fontSize = 32.sp
        )
        Text(
            text = profession.title,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ========================================================================
// Interview Welcome Screen (with profession, HR name, company)
// ========================================================================

@Composable
private fun InterviewWelcomeScreen(
    profession: String,
    hrName: String,
    companyName: String,
    onStart: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Симуляція",
                    style = AppTypography.labelMedium,
                    color = TextColors.onDarkSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "\uD83D\uDCBC Співбесіда",
                    style = AppTypography.displayLarge,
                    color = TextColors.onDarkPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interview info card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.18f))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Готовий до співбесіди?",
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                // Profession & company info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F0FF), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "\uD83C\uDFAF", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "Позиція",
                                style = AppTypography.bodySmall,
                                color = TextColors.onLightSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = profession,
                                style = AppTypography.bodyMedium,
                                color = TextColors.onLightPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "\uD83C\uDFE2", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "Компанія",
                                style = AppTypography.bodySmall,
                                color = TextColors.onLightSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = companyName,
                                style = AppTypography.bodyMedium,
                                color = TextColors.onLightPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "\uD83D\uDC64", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "HR-менеджер",
                                style = AppTypography.bodySmall,
                                color = TextColors.onLightSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = hrName,
                                style = AppTypography.bodyMedium,
                                color = TextColors.onLightPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "$hrName з $companyName проведе з тобою повноцінну співбесіду з 5 раундів на позицію \"$profession\".",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "\uD83D\uDCA1 Поради:",
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Використовуй STAR метод\n• Будь конкретним та чесним\n• Покажи свої сильні сторони\n• Говори впевнено та чітко",
                            style = AppTypography.bodySmall,
                            color = TextColors.onLightSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "\uD83C\uDFAF Почати співбесіду",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

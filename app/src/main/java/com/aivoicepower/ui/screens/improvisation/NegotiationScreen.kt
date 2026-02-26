package com.aivoicepower.ui.screens.improvisation

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.components.FocusCountdownOverlay
import com.aivoicepower.ui.screens.improvisation.components.AnalyzingScreen
import com.aivoicepower.ui.components.AnalysisResultsContent
import com.aivoicepower.ui.screens.improvisation.components.OrbState
import com.aivoicepower.ui.screens.improvisation.components.VoiceExerciseScreen
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.components.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun NegotiationScreen(
    viewModel: NegotiationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
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
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Для виходу натисніть Назад ще раз",
                    duration = SnackbarDuration.Short
                )
            }
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
                        onDismiss = { viewModel.onEvent(NegotiationEvent.DismissAnalysis); onNavigateBack() },
                        dismissButtonText = "Готово"
                    )
                }
            }
        }

        state.isAnalyzing -> {
            AnalyzingScreen()
        }

        !state.isStarted -> {
            NegotiationWelcomeScreen(
                selectedScenario = state.selectedScenario,
                onScenarioSelected = { viewModel.onEvent(NegotiationEvent.ScenarioSelected(it)) },
                onStart = { viewModel.onEvent(NegotiationEvent.StartSimulation) },
                onNavigateBack = onNavigateBack
            )
        }

        state.isStarted && showFocus -> {
            FocusCountdownOverlay(
                exerciseName = "Перемовини",
                onComplete = { showFocus = false; focusDone = true; viewModel.onEvent(NegotiationEvent.CountdownComplete) }
            )
        }

        state.currentRound <= state.maxRounds -> {
            VoiceExerciseScreen(
                title = "Перемовини",
                stepInfo = "Крок ${state.currentRound}/${state.maxRounds}",
                roleEmoji = "\uD83E\uDD1D",
                roleName = state.selectedScenario?.aiRole ?: "Бiзнес-партнер",
                aiText = state.aiText,
                hint = state.hint,
                orbState = state.orbState,
                audioLevel = state.audioLevel,
                isRecording = state.isRecording,
                recordingSeconds = state.recordingSeconds,
                onRecordClick = { viewModel.onEvent(NegotiationEvent.StartRecording) },
                onStopClick = { viewModel.onEvent(NegotiationEvent.StopRecording) },
                onBackClick = onNavigateBack,
                errorMessage = state.error
            )
        }

        else -> {
            VoiceExerciseScreen(
                title = "Перемовини",
                stepInfo = "Завершено",
                roleEmoji = "\uD83E\uDD1D",
                roleName = state.selectedScenario?.aiRole ?: "Бiзнес-партнер",
                aiText = state.aiText.ifBlank { "Перемовини завершенi. Дякуємо за продуктивну дискусiю!" },
                hint = null,
                orbState = state.orbState,
                onRecordClick = {},
                onBackClick = { viewModel.onEvent(NegotiationEvent.FinishNegotiation); onNavigateBack() },
                onAnalyzeClick = { viewModel.onEvent(NegotiationEvent.AnalyzeClicked) },
                onSkipClick = { viewModel.onEvent(NegotiationEvent.SkipClicked); onNavigateBack() },
                errorMessage = state.error
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF667EEA),
                contentColor = Color.White
            )
        }
    }
}

private val NEGOTIATION_SCENARIOS = listOf(
    NegotiationScenario(
        name = "Оренда офісу",
        aiRole = "Власник приміщення",
        aiPosition = "Хоче здати офіс за 45 000 грн/міс з мінімальним договором на 2 роки",
        userGoal = "Домовтеся про нижчу ціну або кращі умови"
    ),
    NegotiationScenario(
        name = "Постачання товарів",
        aiRole = "Постачальник",
        aiPosition = "Пропонує партію з 500 одиниць по 120 грн/шт, мінімальне замовлення",
        userGoal = "Зменшіть ціну або мінімальну партію"
    ),
    NegotiationScenario(
        name = "Підвищення зарплати",
        aiRole = "Керівник відділу",
        aiPosition = "Бюджет обмежений, може додати до 10% або нефінансові бонуси",
        userGoal = "Домовтеся про підвищення зарплати або кращі умови"
    ),
    NegotiationScenario(
        name = "Маркетингова кампанія",
        aiRole = "Маркетинг-директор",
        aiPosition = "Має бюджет 200 000 грн, хоче максимум охоплення за мінімальну ціну",
        userGoal = "Продайте свої послуги з максимальною вигодою"
    ),
    NegotiationScenario(
        name = "IT-аутсорсинг",
        aiRole = "CTO компанії",
        aiPosition = "Потребує розробку за 3 місяці, бюджет 500 000 грн, хоче фіксовану ціну",
        userGoal = "Узгодьте обсяг, строки та оплату"
    ),
    NegotiationScenario(
        name = "Партнерська угода",
        aiRole = "Директор компанії",
        aiPosition = "Хоче 60% прибутку та повний контроль над маркетингом",
        userGoal = "Домовтеся про справедливий розподіл та зони відповідальності"
    ),
    NegotiationScenario(
        name = "Інвестиція в стартап",
        aiRole = "Інвестор",
        aiPosition = "Готовий вкласти 2 млн грн, але хоче 40% частки та місце в раді",
        userGoal = "Отримайте інвестицію зі збереженням контролю"
    ),
    NegotiationScenario(
        name = "Закупівля обладнання",
        aiRole = "Менеджер з продажу",
        aiPosition = "Продає обладнання за 800 000 грн, гарантія 1 рік, без доставки",
        userGoal = "Знизьте ціну або отримайте кращі умови"
    ),
    NegotiationScenario(
        name = "Організація заходу",
        aiRole = "Менеджер івент-агенції",
        aiPosition = "Пропонує пакет за 150 000 грн на 100 гостей, базове оформлення",
        userGoal = "Отримайте більше послуг за вашу ціну"
    ),
    NegotiationScenario(
        name = "Франчайза ресторану",
        aiRole = "Франчайзер",
        aiPosition = "Вступний внесок 1 млн грн, роялті 8%, суворі стандарти бренду",
        userGoal = "Домовтеся про вигідніші умови франчайзи"
    )
)

@Composable
private fun NegotiationWelcomeScreen(
    selectedScenario: NegotiationScenario?,
    onScenarioSelected: (NegotiationScenario) -> Unit,
    onStart: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        text = "\uD83E\uDD1D Перемовини",
                        style = AppTypography.displayLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp
                    )
                }

                Row(
                    modifier = Modifier
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onNavigateBack() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\u2190", fontSize = 24.sp, color = Color(0xFF667EEA), fontWeight = FontWeight.Bold)
                    Text(text = "\u041D\u0430\u0437\u0430\u0434", style = AppTypography.bodyMedium, color = TextColors.onLightPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Scenario selection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.18f))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Обери сценарій переговорів",
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                NEGOTIATION_SCENARIOS.forEach { scenario ->
                    val isSelected = selectedScenario == scenario
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFF667EEA).copy(alpha = 0.1f) else Color(0xFFF3F4F6),
                                RoundedCornerShape(12.dp)
                            )
                            .then(
                                if (isSelected) Modifier.border(1.5.dp, Color(0xFF667EEA), RoundedCornerShape(12.dp))
                                else Modifier
                            )
                            .clickable { onScenarioSelected(scenario) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = scenario.name,
                            style = AppTypography.bodyMedium,
                            color = if (isSelected) Color(0xFF667EEA) else TextColors.onLightPrimary,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                        )
                        Text(
                            text = "${scenario.aiRole}: ${scenario.aiPosition}",
                            style = AppTypography.bodySmall,
                            color = if (isSelected) Color(0xFF667EEA).copy(alpha = 0.7f) else TextColors.onLightSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "\uD83E\uDD1D Почати переговори",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

package com.aivoicepower.ui.screens.improvisation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.content.SalesProductsProvider
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
import kotlinx.coroutines.launch

@Composable
fun SalesPitchScreen(
    viewModel: SalesPitchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
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

    LaunchedEffect(state.phase) {
        if (state.phase == SalesPhase.Conversation && !focusDone) {
            showFocus = true
        }
    }

    BackHandler(enabled = state.phase == SalesPhase.Conversation) {
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
                        onDismiss = { viewModel.onEvent(SalesPitchEvent.DismissAnalysis); onNavigateBack() },
                        dismissButtonText = "Готово"
                    )
                }
            }
        }

        state.isAnalyzing -> {
            AnalyzingScreen()
        }

        state.phase == SalesPhase.ProductSelection -> {
            ProductSelectionScreen(
                onProductSelected = { viewModel.onEvent(SalesPitchEvent.ProductSelected(it)) },
                onNavigateBack = onNavigateBack
            )
        }

        state.phase == SalesPhase.CustomerProfile -> {
            CustomerProfileScreen(
                product = state.selectedProduct!!,
                customer = state.customerProfile!!,
                onStartPitch = { viewModel.onEvent(SalesPitchEvent.StartPitchClicked) },
                onNavigateBack = onNavigateBack
            )
        }

        state.phase == SalesPhase.Conversation && showFocus -> {
            FocusCountdownOverlay(
                exerciseName = "Продай товар",
                topic = state.selectedProduct?.name ?: "",
                onComplete = { showFocus = false; focusDone = true; viewModel.onEvent(SalesPitchEvent.CountdownComplete) }
            )
        }

        state.phase == SalesPhase.Conversation -> {
            VoiceExerciseScreen(
                title = "Продай товар",
                stepInfo = "Крок ${state.currentRound}/${state.maxRounds}",
                roleEmoji = "\uD83D\uDED2",
                roleName = state.customerProfile?.type ?: "Клiєнт",
                aiText = state.aiText,
                hint = state.hint,
                orbState = state.orbState,
                audioLevel = state.audioLevel,
                isRecording = state.isRecording,
                recordingSeconds = state.recordingSeconds,
                onRecordClick = { viewModel.onEvent(SalesPitchEvent.StartRecordingClicked) },
                onStopClick = { viewModel.onEvent(SalesPitchEvent.StopRecordingClicked) },
                onBackClick = onNavigateBack,
                errorMessage = state.error
            )
        }

        state.phase == SalesPhase.Complete -> {
            VoiceExerciseScreen(
                title = "Продай товар",
                stepInfo = "Завершено",
                roleEmoji = "\uD83D\uDED2",
                roleName = state.customerProfile?.type ?: "Клiєнт",
                aiText = state.aiText,
                hint = null,
                orbState = state.orbState,
                onRecordClick = {},
                onBackClick = { viewModel.onEvent(SalesPitchEvent.FinishClicked); onNavigateBack() },
                onAnalyzeClick = { viewModel.onEvent(SalesPitchEvent.AnalyzeClicked) },
                onSkipClick = { viewModel.onEvent(SalesPitchEvent.SkipClicked); onNavigateBack() },
                errorMessage = state.error
            )
        }
    }

    // Snackbar
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

@Composable
private fun ProductSelectionScreen(
    onProductSelected: (SalesProductsProvider.SalesProduct) -> Unit,
    onNavigateBack: () -> Unit
) {
    val view = LocalView.current
    val products = remember { SalesProductsProvider().getAllProducts(includeAbsurd = true) }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 30.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Імпровізація",
                        style = AppTypography.labelMedium,
                        color = TextColors.onDarkSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "\uD83D\uDCBC Продай товар",
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
                    Text(text = "←", fontSize = 24.sp, color = Color(0xFF667EEA), fontWeight = FontWeight.Bold)
                    Text(text = "Назад", style = AppTypography.bodyMedium, color = TextColors.onLightPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Обери товар для продажу:",
                        style = AppTypography.titleLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(products) { product ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = if (product.isAbsurd) Color(0xFFA855F7).copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.12f))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onProductSelected(product) }
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = product.name, style = AppTypography.titleMedium, color = TextColors.onLightPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            if (product.isAbsurd) {
                                Text(text = "\uD83C\uDFAA Абсурдний", style = AppTypography.labelSmall, color = Color(0xFFA855F7), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Text(text = product.description, style = AppTypography.bodyMedium, color = TextColors.onLightSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Ціна: ${product.price}", style = AppTypography.labelLarge, color = Color(0xFF6366F1), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerProfileScreen(
    product: SalesProductsProvider.SalesProduct,
    customer: SalesProductsProvider.CustomerProfile,
    onStartPitch: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Product card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.18f))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Твій товар:", style = AppTypography.labelMedium, color = Color(0xFF667EEA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = product.name, style = AppTypography.titleLarge, color = TextColors.onLightPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = product.description, style = AppTypography.bodyMedium, color = TextColors.onLightSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(text = product.price, style = AppTypography.titleMedium, color = Color(0xFF667EEA), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // Customer card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.18f))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Твій клієнт:", style = AppTypography.labelMedium, color = Color(0xFF764BA2), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = customer.type, style = AppTypography.titleLarge, color = TextColors.onLightPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = customer.description, style = AppTypography.bodyMedium, color = TextColors.onLightSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(text = "Типові заперечення:", style = AppTypography.labelMedium, color = TextColors.onLightPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                customer.typicalObjections.forEach { objection ->
                    Text(text = "• $objection", style = AppTypography.bodySmall, color = TextColors.onLightSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            PrimaryButton(
                text = "\uD83C\uDFA4 Почати продаж",
                onClick = onStartPitch,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

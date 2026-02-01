package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.content.SalesProductsProvider
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.components.PrimaryButton

@Composable
fun SalesPitchScreen(
    viewModel: SalesPitchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp)
        ) {
            // Header with back button
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
                        text = "💼 Продай товар",
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
                        .clickable { onNavigateBack() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "←",
                        fontSize = 24.sp,
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Назад",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
            when (state.phase) {
                SalesPhase.ProductSelection -> {
                    ProductSelectionContent(
                        onProductSelected = { viewModel.onEvent(SalesPitchEvent.ProductSelected(it)) }
                    )
                }

                SalesPhase.CustomerProfile -> {
                    CustomerProfileContent(
                        product = state.selectedProduct!!,
                        customer = state.customerProfile!!,
                        onStartPitch = { viewModel.onEvent(SalesPitchEvent.StartPitchClicked) }
                    )
                }

                SalesPhase.OpeningPitch -> {
                    PitchRecordingContent(
                        product = state.selectedProduct!!,
                        isRecording = state.isRecording,
                        secondsElapsed = state.recordingSeconds,
                        maxSeconds = state.maxRecordingSeconds,
                        onStartRecording = { viewModel.onEvent(SalesPitchEvent.StartRecordingClicked) },
                        onStopRecording = { viewModel.onEvent(SalesPitchEvent.StopRecordingClicked) }
                    )
                }

                SalesPhase.CustomerReaction -> {
                    CustomerReactionContent(
                        isThinking = state.isAiThinking,
                        customerResponse = state.customerResponse,
                        onContinue = { viewModel.onEvent(SalesPitchEvent.ContinueToObjectionClicked) }
                    )
                }

                SalesPhase.HandlingObjection -> {
                    ObjectionHandlingContent(
                        customerResponse = state.customerResponse ?: "",
                        isRecording = state.isRecording,
                        secondsElapsed = state.recordingSeconds,
                        maxSeconds = 60,
                        onStartRecording = { viewModel.onEvent(SalesPitchEvent.StartRecordingClicked) },
                        onStopRecording = { viewModel.onEvent(SalesPitchEvent.StopRecordingClicked) }
                    )
                }

                SalesPhase.FinalDecision -> {
                    FinalDecisionContent(
                        isThinking = state.isAiThinking,
                        decision = state.finalDecision,
                        onFinish = { viewModel.onEvent(SalesPitchEvent.FinishSalesClicked) }
                    )
                }

                SalesPhase.SalesComplete -> {
                    SalesCompleteContent(
                        product = state.selectedProduct!!,
                        decision = state.finalDecision ?: "",
                        onFinish = onNavigateBack
                    )
                }
            }

                // Error message
                state.error?.let { error ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = Color(0xFFEF4444).copy(alpha = 0.2f)
                            )
                            .background(Color(0xFFFEF2F2), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = error,
                            style = AppTypography.bodyMedium,
                            color = Color(0xFFDC2626),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSelectionContent(
    onProductSelected: (SalesProductsProvider.SalesProduct) -> Unit
) {
    val products = remember { SalesProductsProvider().getAllProducts(includeAbsurd = true) }

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
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = if (product.isAbsurd) {
                            Color(0xFFA855F7).copy(alpha = 0.2f)
                        } else {
                            Color.Black.copy(alpha = 0.12f)
                        }
                    )
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .clickable { onProductSelected(product) }
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.name,
                        style = AppTypography.titleMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (product.isAbsurd) {
                        Text(
                            text = "🎪 Абсурдний",
                            style = AppTypography.labelSmall,
                            color = Color(0xFFA855F7),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = product.description,
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Ціна: ${product.price}",
                    style = AppTypography.labelLarge,
                    color = Color(0xFF6366F1),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CustomerProfileContent(
    product: SalesProductsProvider.SalesProduct,
    customer: SalesProductsProvider.CustomerProfile,
    onStartPitch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Твій товар:",
                style = AppTypography.labelMedium,
                color = Color(0xFF667EEA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.name,
                style = AppTypography.titleLarge,
                color = TextColors.onLightPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = product.description,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = product.price,
                style = AppTypography.titleMedium,
                color = Color(0xFF667EEA),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Твій клієнт:",
                style = AppTypography.labelMedium,
                color = Color(0xFF764BA2),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = customer.type,
                style = AppTypography.titleLarge,
                color = TextColors.onLightPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = customer.description,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Типові заперечення:",
                style = AppTypography.labelMedium,
                color = TextColors.onLightPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            customer.typicalObjections.forEach { objection ->
                Text(
                    text = "• $objection",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Твоє завдання:",
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "1. Презентуй товар (до 90 сек)\n2. Обробь заперечення клієнта\n3. Закрий продаж",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        PrimaryButton(
            text = "🎤 Почати презентацію",
            onClick = onStartPitch,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PitchRecordingContent(
    product: SalesProductsProvider.SalesProduct,
    isRecording: Boolean,
    secondsElapsed: Int,
    maxSeconds: Int,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Продаєш: ${product.name}",
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.price,
                style = AppTypography.labelLarge,
                color = Color(0xFF667EEA),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isRecording) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0xFFEF4444).copy(alpha = 0.3f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2))
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🔴 Запис презентації...",
                    style = AppTypography.headlineSmall,
                    color = Color(0xFFEF4444),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$secondsElapsed / $maxSeconds сек",
                    style = AppTypography.displayMedium,
                    color = Color(0xFF991B1B),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                )
                LinearProgressIndicator(
                    progress = { secondsElapsed.toFloat() / maxSeconds },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = Color(0xFFEF4444),
                    trackColor = Color.White.copy(alpha = 0.5f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                PrimaryButton(
                    text = "■ Завершити презентацію",
                    onClick = onStopRecording,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Презентуй товар:",
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Розкажи про переваги, унікальність, вартість. Переконай клієнта!",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "🎤 Почати запис",
                onClick = onStartRecording,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CustomerReactionContent(
    isThinking: Boolean,
    customerResponse: String?,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isThinking) {
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = Color(0xFF667EEA))
                Text(
                    text = "Клієнт обдумує...",
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Реакція клієнта:",
                    style = AppTypography.labelLarge,
                    color = Color(0xFF764BA2),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = customerResponse ?: "",
                    style = AppTypography.bodyLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Відповісти на заперечення",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ObjectionHandlingContent(
    customerResponse: String,
    isRecording: Boolean,
    secondsElapsed: Int,
    maxSeconds: Int,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Заперечення клієнта:",
                style = AppTypography.labelMedium,
                color = Color(0xFF764BA2),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = customerResponse,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (isRecording) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0xFFEF4444).copy(alpha = 0.3f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2))
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🔴 Запис відповіді...",
                    style = AppTypography.headlineSmall,
                    color = Color(0xFFEF4444),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$secondsElapsed / $maxSeconds сек",
                    style = AppTypography.displayMedium,
                    color = Color(0xFF991B1B),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                )
                LinearProgressIndicator(
                    progress = { secondsElapsed.toFloat() / maxSeconds },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = Color(0xFFEF4444),
                    trackColor = Color.White.copy(alpha = 0.5f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                PrimaryButton(
                    text = "■ Завершити відповідь",
                    onClick = onStopRecording,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
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
                    .padding(20.dp)
            ) {
                Text(
                    text = "Обробити заперечення клієнта та переконай купити!",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "🎤 Відповісти",
                onClick = onStartRecording,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FinalDecisionContent(
    isThinking: Boolean,
    decision: String?,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isThinking) {
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = Color(0xFF667EEA))
                Text(
                    text = "Клієнт приймає рішення...",
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            val isPositive = decision?.contains("купую", ignoreCase = true) == true ||
                           decision?.contains("так", ignoreCase = true) == true
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Рішення клієнта:",
                    style = AppTypography.labelLarge,
                    color = if (isPositive) Color(0xFF667EEA) else Color(0xFFEF4444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = decision ?: "",
                    style = AppTypography.bodyLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Завершити",
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SalesCompleteContent(
    product: SalesProductsProvider.SalesProduct,
    decision: String,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "✓ Продаж завершено!",
                style = AppTypography.headlineMedium,
                color = Color(0xFF667EEA),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Товар: ${product.name}",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Результат:",
                style = AppTypography.labelLarge,
                color = Color(0xFF667EEA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = decision,
                style = AppTypography.bodyLarge,
                color = TextColors.onLightPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        PrimaryButton(
            text = "Готово",
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

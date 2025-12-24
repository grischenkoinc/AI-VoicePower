package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.content.SalesProductsProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesPitchScreen(
    viewModel: SalesPitchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💼 Продаж товару") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductSelectionContent(
    onProductSelected: (SalesProductsProvider.SalesProduct) -> Unit
) {
    val products = remember { SalesProductsProvider().getAllProducts(includeAbsurd = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Обери товар для продажу:",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(products) { product ->
            Card(
                onClick = { onProductSelected(product) },
                modifier = Modifier.fillMaxWidth(),
                colors = if (product.isAbsurd) {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                } else {
                    CardDefaults.cardColors()
                }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (product.isAbsurd) {
                            Text(
                                text = "🎪 Абсурдний",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Ціна: ${product.price}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Твій товар:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Твій клієнт:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = customer.type,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = customer.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Типові заперечення:",
                    style = MaterialTheme.typography.labelMedium
                )
                customer.typicalObjections.forEach { objection ->
                    Text(
                        text = "• $objection",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Твоє завдання:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "1. Презентуй товар (до 90 сек)\n2. Обробь заперечення клієнта\n3. Закрий продаж",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = onStartPitch,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🎤 Почати презентацію")
        }
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
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Продаєш: ${product.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (isRecording) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🔴 Запис презентації...",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "$secondsElapsed / $maxSeconds сек",
                        style = MaterialTheme.typography.displayMedium
                    )
                    LinearProgressIndicator(
                        progress = secondsElapsed.toFloat() / maxSeconds,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = onStopRecording,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("■ Завершити презентацію")
                    }
                }
            }
        } else {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Презентуй товар:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Розкажи про переваги, унікальність, вартість. Переконай клієнта!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = onStartRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎤 Почати запис")
            }
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
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Клієнт обдумує...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Реакція клієнта:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = customerResponse ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Відповісти на заперечення")
            }
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
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Заперечення клієнта:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = customerResponse,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (isRecording) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🔴 Запис відповіді...",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "$secondsElapsed / $maxSeconds сек",
                        style = MaterialTheme.typography.displayMedium
                    )
                    LinearProgressIndicator(
                        progress = secondsElapsed.toFloat() / maxSeconds,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = onStopRecording,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("■ Завершити відповідь")
                    }
                }
            }
        } else {
            Card {
                Text(
                    text = "Обробити заперечення клієнта та переконай купити!",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onStartRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎤 Відповісти")
            }
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
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Клієнт приймає рішення...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (decision?.contains("купую", ignoreCase = true) == true ||
                                         decision?.contains("так", ignoreCase = true) == true)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Рішення клієнта:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = decision ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Завершити")
            }
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
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✓ Продаж завершено!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Товар: ${product.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Результат:",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = decision,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Готово")
        }
    }
}

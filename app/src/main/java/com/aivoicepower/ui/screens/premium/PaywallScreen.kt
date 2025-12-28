package com.aivoicepower.ui.screens.premium

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.premium.components.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel = hiltViewModel(),
    source: PaywallSource = PaywallSource.UNKNOWN,
    onNavigateBack: () -> Unit,
    onPurchaseSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(source) {
        viewModel.setSource(source)
    }

    // Handle purchase success
    LaunchedEffect(state.isPremium) {
        if (state.isPremium) {
            onPurchaseSuccess()
        }
    }

    // Show error snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            delay(100)
            viewModel.onEvent(PaywallEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Преміум") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            activity?.let { viewModel.launchPurchase(it) }
                                ?: viewModel.onEvent(PaywallEvent.PurchaseClicked)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "Оформити ${state.selectedPlan.price}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { viewModel.onEvent(PaywallEvent.RestorePurchases) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Відновити покупки")
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Розкрийте повний потенціал свого голосу",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = getPaywallMessage(state.source),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pricing Cards
            PricingPlan.entries.forEach { plan ->
                PricingCard(
                    plan = plan,
                    isSelected = state.selectedPlan == plan,
                    onSelect = { viewModel.onEvent(PaywallEvent.SelectPlan(plan)) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Features
            Text(
                text = "Що входить у Преміум",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            PremiumBenefitItem(
                icon = Icons.Default.Lock,
                title = "Всі курси",
                description = "Повний доступ до 6 курсів (126 уроків)"
            )

            PremiumBenefitItem(
                icon = Icons.Default.Mic,
                title = "Необмежена імпровізація",
                description = "Тренуйте спонтанне мовлення без обмежень"
            )

            PremiumBenefitItem(
                icon = Icons.Default.SmartToy,
                title = "AI-тренер без лімітів",
                description = "Необмежена кількість повідомлень з AI"
            )

            PremiumBenefitItem(
                icon = Icons.Default.Assessment,
                title = "Повна аналітика",
                description = "Детальна статистика та порівняння \"до/після\""
            )

            PremiumBenefitItem(
                icon = Icons.Default.CloudOff,
                title = "Офлайн режим",
                description = "Практикуйте без інтернету"
            )

            PremiumBenefitItem(
                icon = Icons.Default.Block,
                title = "Без реклами",
                description = "Ніяких відволікань"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Comparison Card
            FeatureComparisonCard()

            Spacer(modifier = Modifier.height(32.dp))

            // Terms
            Text(
                text = "Підписка автоматично поновлюється. Ви можете скасувати в будь-який момент в налаштуваннях Google Play.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom bar
        }
    }
}

private fun getPaywallMessage(source: PaywallSource): String {
    return when (source) {
        PaywallSource.COURSE_LOCKED ->
            "Перші 7 уроків безкоштовні. Оформіть Преміум для доступу до всіх уроків!"
        PaywallSource.IMPROV_LIMIT ->
            "Ви досягли денного ліміту (3 сесії). Преміум дає необмежений доступ!"
        PaywallSource.AI_COACH_LIMIT ->
            "Ви використали 10 повідомлень сьогодні. Преміум знімає всі обмеження!"
        PaywallSource.DIAGNOSTIC_LIMIT ->
            "Повторна діагностика доступна тільки у Преміум версії"
        PaywallSource.SETTINGS ->
            "Отримайте повний доступ до всіх функцій"
        PaywallSource.ACHIEVEMENT ->
            "Розблокуйте всі досягнення з Преміум"
        PaywallSource.UNKNOWN ->
            "Отримайте повний доступ до всіх функцій"
    }
}

// Extension to find activity
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

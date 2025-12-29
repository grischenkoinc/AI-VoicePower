package com.aivoicepower.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Налаштування") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Premium Section
            SettingsItem(
                icon = Icons.Default.Star,
                title = "Преміум",
                subtitle = "Отримайте повний доступ",
                onClick = onNavigateToPremium
            )

            Divider()

            // Notifications
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Нагадування",
                subtitle = "Налаштуйте час тренувань",
                onClick = { /* TODO */ }
            )

            // Language
            SettingsItem(
                icon = Icons.Default.Language,
                title = "Мова",
                subtitle = "Українська",
                onClick = { /* TODO */ }
            )

            // Theme
            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Тема",
                subtitle = "Системна",
                onClick = { /* TODO */ }
            )

            Divider()

            // About
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Про застосунок",
                subtitle = "Версія 1.0.0",
                onClick = { /* TODO */ }
            )

            // Privacy
            SettingsItem(
                icon = Icons.Default.PrivacyTip,
                title = "Політика конфіденційності",
                onClick = { /* TODO */ }
            )

            // Terms
            SettingsItem(
                icon = Icons.Default.Description,
                title = "Умови використання",
                onClick = { /* TODO */ }
            )

            Divider()

            // Clear Data
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Очистити дані",
                subtitle = "Видалити записи та прогрес",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

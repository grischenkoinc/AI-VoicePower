package com.aivoicepower.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.settings.components.*
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Dialogs
    if (state.showLogoutDialog) {
        ConfirmationDialog(
            title = "Вихід з акаунту",
            message = "Ви впевнені, що хочете вийти? Дані на цьому пристрої збережуться.",
            confirmText = "Вийти",
            isDangerous = false,
            onConfirm = { viewModel.onEvent(SettingsEvent.ConfirmLogout) },
            onDismiss = { viewModel.onEvent(SettingsEvent.DismissDialog) }
        )
    }

    if (state.showDeleteAccountDialog) {
        ConfirmationDialog(
            title = "Видалити акаунт",
            message = "Ви впевнені? Це видалить ваш акаунт та всі дані з хмари. Цю дію не можна скасувати.",
            confirmText = "Видалити",
            isDangerous = true,
            onConfirm = { viewModel.onEvent(SettingsEvent.ConfirmDeleteAccount) },
            onDismiss = { viewModel.onEvent(SettingsEvent.DismissDialog) }
        )
    }

    if (state.showClearDataDialog) {
        ConfirmationDialog(
            title = "Очистити дані",
            message = "Це видалить всі записи та прогрес з цього пристрою. Дані у хмарі залишаться.",
            confirmText = "Очистити",
            isDangerous = true,
            onConfirm = { viewModel.onEvent(SettingsEvent.ConfirmClearData) },
            onDismiss = { viewModel.onEvent(SettingsEvent.DismissDialog) }
        )
    }

    GradientBackground(content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Styled top bar with glass back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glass morphism back button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(alpha = 0.12f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.15f),
                            CircleShape
                        )
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Налаштування",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    // Subtle gradient underline
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF667EEA),
                                        Color(0xFF764BA2)
                                    )
                                ),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. User Profile Card
            UserProfileCard(
                userName = state.userName,
                userEmail = state.userEmail,
                isAuthenticated = state.isAuthenticated,
                onLoginClick = onNavigateToAuth,
                onEditProfileClick = { /* TODO: edit profile */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Premium Card
            PremiumSettingsCard(
                isPremium = state.isPremium,
                onClick = onNavigateToPremium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Training Section
            SettingsSection(title = "Тренування") {
                SettingsItemRow(
                    icon = Icons.Default.Notifications,
                    title = "Нагадування",
                    subtitle = if (state.isReminderEnabled) "Увімкнено" else "Вимкнено",
                    trailingType = SettingsTrailingType.SWITCH,
                    isChecked = state.isReminderEnabled,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleReminder) },
                    onClick = { viewModel.onEvent(SettingsEvent.ToggleReminder) }
                )
                SettingsItemRow(
                    icon = Icons.Default.Timer,
                    title = "Щоденна мета",
                    trailingType = SettingsTrailingType.VALUE,
                    valueText = "${state.dailyGoalMinutes} хв",
                    onClick = { viewModel.onEvent(SettingsEvent.ShowDailyGoalPicker) }
                )
                SettingsItemRow(
                    icon = Icons.Default.Language,
                    title = "Мова",
                    trailingType = SettingsTrailingType.VALUE,
                    valueText = "Українська",
                    showDivider = false,
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. App Section
            SettingsSection(title = "Застосунок") {
                SettingsItemRow(
                    icon = Icons.Default.Palette,
                    title = "Тема",
                    trailingType = SettingsTrailingType.VALUE,
                    valueText = "Темна",
                    onClick = { /* TODO */ }
                )
                SettingsItemRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    title = "Звукові ефекти",
                    trailingType = SettingsTrailingType.SWITCH,
                    isChecked = state.isSoundEnabled,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleSound) },
                    showDivider = false,
                    onClick = { viewModel.onEvent(SettingsEvent.ToggleSound) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Account Section (only if authenticated)
            if (state.isAuthenticated) {
                SettingsSection(title = "Акаунт") {
                    SettingsItemRow(
                        icon = Icons.Default.Sync,
                        title = "Синхронізація",
                        subtitle = if (state.isSyncEnabled) "Увімкнено" else "Вимкнено",
                        trailingType = SettingsTrailingType.SWITCH,
                        isChecked = state.isSyncEnabled,
                        onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleSync) },
                        onClick = { viewModel.onEvent(SettingsEvent.ToggleSync) }
                    )
                    SettingsItemRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Вийти з акаунту",
                        trailingType = SettingsTrailingType.NONE,
                        onClick = { viewModel.onEvent(SettingsEvent.LogoutClicked) }
                    )
                    SettingsItemRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Видалити акаунт",
                        iconTint = Color(0xFFEF4444),
                        titleColor = Color(0xFFEF4444),
                        trailingType = SettingsTrailingType.NONE,
                        showDivider = false,
                        onClick = { viewModel.onEvent(SettingsEvent.DeleteAccountClicked) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 6. Info Section
            SettingsSection(title = "Інформація") {
                SettingsItemRow(
                    icon = Icons.Default.Info,
                    title = "Про застосунок",
                    subtitle = "Версія 1.0.0",
                    onClick = { /* TODO */ }
                )
                SettingsItemRow(
                    icon = Icons.Default.PrivacyTip,
                    title = "Політика конфіденційності",
                    onClick = { /* TODO */ }
                )
                SettingsItemRow(
                    icon = Icons.Default.Description,
                    title = "Умови використання",
                    onClick = { /* TODO */ }
                )
                SettingsItemRow(
                    icon = Icons.Default.Feedback,
                    title = "Зворотний зв'язок",
                    showDivider = false,
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Danger Zone
            SettingsSection(title = "Дані") {
                SettingsItemRow(
                    icon = Icons.Default.Delete,
                    title = "Очистити дані",
                    subtitle = "Видалити записи та прогрес",
                    iconTint = Color(0xFFEF4444),
                    trailingType = SettingsTrailingType.NONE,
                    showDivider = false,
                    onClick = { viewModel.onEvent(SettingsEvent.ClearDataClicked) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App version footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI VoicePower v1.0.0",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    })
}

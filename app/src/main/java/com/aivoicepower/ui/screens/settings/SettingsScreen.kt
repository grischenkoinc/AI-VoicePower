package com.aivoicepower.ui.screens.settings

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalView
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
    onNavigateToAbout: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    onDataCleared: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val view = LocalView.current
    var wasAuthenticated by remember { mutableStateOf(state.isAuthenticated) }

    // Navigate to auth screen after logout/delete
    LaunchedEffect(state.isAuthenticated) {
        if (wasAuthenticated && !state.isAuthenticated) {
            onLoggedOut()
        }
        wasAuthenticated = state.isAuthenticated
    }

    // Navigate to onboarding after data cleared
    LaunchedEffect(state.navigateToOnboarding) {
        if (state.navigateToOnboarding) {
            onDataCleared()
        }
    }

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
            message = "Це назавжди видалить ваш акаунт, весь прогрес, записи та дані з хмари. Цю дію буде неможливо відмінити!",
            confirmText = "Видалити назавжди",
            isDangerous = true,
            onConfirm = { viewModel.onEvent(SettingsEvent.ConfirmDeleteAccount) },
            onDismiss = { viewModel.onEvent(SettingsEvent.DismissDialog) }
        )
    }

    if (state.showClearDataDialog) {
        ConfirmationDialog(
            title = "Очистити всі дані",
            message = "Це назавжди видалить весь ваш прогрес, записи, результати діагностики та досягнення — і з пристрою, і з хмари. Цю дію буде неможливо відмінити!",
            confirmText = "Очистити все",
            isDangerous = true,
            onConfirm = { viewModel.onEvent(SettingsEvent.ConfirmClearData) },
            onDismiss = { viewModel.onEvent(SettingsEvent.DismissDialog) }
        )
    }

    if (state.showDailyGoalPicker) {
        DailyGoalPicker(
            currentGoalMinutes = state.dailyGoalMinutes,
            onGoalSelected = { viewModel.onEvent(SettingsEvent.SetDailyGoal(it)) },
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

            // Styled top bar with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = Color.Black.copy(alpha = 0.08f)
                        )
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable(onClick = { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onNavigateBack() }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color(0xFF1A1A2E),
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
                    showDivider = false,
                    onClick = { viewModel.onEvent(SettingsEvent.ShowDailyGoalPicker) }
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
                    showDivider = state.isSoundEnabled,
                    onClick = { viewModel.onEvent(SettingsEvent.ToggleSound) }
                )
                AnimatedVisibility(
                    visible = state.isSoundEnabled,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(start = 68.dp, end = 16.dp, bottom = 8.dp)
                    ) {
                        SoundVolumeRow(
                            label = "Навігація",
                            value = state.uiVolume,
                            onValueChange = { viewModel.onEvent(SettingsEvent.SetUiVolume(it)) }
                        )
                        SoundVolumeRow(
                            label = "Зворотний зв'язок",
                            value = state.feedbackVolume,
                            onValueChange = { viewModel.onEvent(SettingsEvent.SetFeedbackVolume(it)) }
                        )
                        SoundVolumeRow(
                            label = "Святкування",
                            value = state.celebrationVolume,
                            onValueChange = { viewModel.onEvent(SettingsEvent.SetCelebrationVolume(it)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Info Section
            SettingsSection(title = "Інформація") {
                SettingsItemRow(
                    icon = Icons.Default.Info,
                    title = "Про застосунок",
                    subtitle = "Версія 1.1.1",
                    onClick = onNavigateToAbout
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

            // 6. Account Section (only if authenticated)
            if (state.isAuthenticated) {
                SettingsSection(title = "Акаунт") {
                    SettingsItemRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Вийти з акаунту",
                        trailingType = SettingsTrailingType.NONE,
                        onClick = { viewModel.onEvent(SettingsEvent.LogoutClicked) }
                    )
                    SettingsItemRow(
                        icon = Icons.Default.Delete,
                        title = "Очистити дані",
                        subtitle = "Видалити записи та прогрес",
                        iconTint = Color(0xFFEF4444),
                        trailingType = SettingsTrailingType.NONE,
                        onClick = { viewModel.onEvent(SettingsEvent.ClearDataClicked) }
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

            Spacer(modifier = Modifier.height(4.dp))

            // App version footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Diqto v1.1.1",
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

@Composable
private fun SoundVolumeRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF9CA3AF),
            modifier = Modifier.width(120.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF8B5CF6),
                activeTrackColor = Color(0xFF8B5CF6),
                inactiveTrackColor = Color(0xFFE5E7EB)
            )
        )
        Text(
            text = "${(value * 100).toInt()}%",
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF),
            modifier = Modifier.width(36.dp)
        )
    }
}

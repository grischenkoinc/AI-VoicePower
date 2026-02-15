package com.aivoicepower.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.repository.AuthRepository
import com.aivoicepower.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            combine(
                authRepository.currentUser,
                userPreferencesDataStore.userPreferencesFlow,
                userPreferencesDataStore.userEmail,
                userPreferencesDataStore.userPhotoUrl
            ) { authUser, prefs, email, photoUrl ->
                _state.update {
                    it.copy(
                        userName = authUser?.displayName ?: prefs.userName,
                        userEmail = authUser?.email ?: email,
                        userPhotoUrl = authUser?.photoUrl ?: photoUrl,
                        isAuthenticated = authUser != null,
                        isPremium = prefs.isPremium,
                        dailyGoalMinutes = prefs.dailyGoalMinutes,
                        isReminderEnabled = prefs.isReminderEnabled
                    )
                }
            }.collect()
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.NavigateToPremium -> { /* handled by UI */ }
            is SettingsEvent.ToggleReminder -> {
                val newEnabled = !_state.value.isReminderEnabled
                _state.update { it.copy(isReminderEnabled = newEnabled) }
                viewModelScope.launch {
                    userPreferencesDataStore.setReminderEnabled(newEnabled)
                    if (newEnabled) {
                        NotificationHelper.scheduleCoachReminder(appContext)
                    } else {
                        NotificationHelper.cancelCoachReminder(appContext)
                    }
                }
            }
            is SettingsEvent.SetDailyGoal -> {
                _state.update { it.copy(dailyGoalMinutes = event.minutes, showDailyGoalPicker = false) }
                viewModelScope.launch {
                    userPreferencesDataStore.setDailyGoalMinutes(event.minutes)
                }
            }
            is SettingsEvent.ShowDailyGoalPicker -> {
                _state.update { it.copy(showDailyGoalPicker = true) }
            }
            is SettingsEvent.ToggleSound -> {
                _state.update { it.copy(isSoundEnabled = !it.isSoundEnabled) }
            }
            is SettingsEvent.ToggleSync -> {
                _state.update { it.copy(isSyncEnabled = !it.isSyncEnabled) }
            }
            is SettingsEvent.LogoutClicked -> {
                _state.update { it.copy(showLogoutDialog = true) }
            }
            is SettingsEvent.ConfirmLogout -> {
                viewModelScope.launch {
                    authRepository.signOut()
                    userPreferencesDataStore.clearAuthData()
                    _state.update { it.copy(showLogoutDialog = false, isAuthenticated = false) }
                }
            }
            is SettingsEvent.DeleteAccountClicked -> {
                _state.update { it.copy(showDeleteAccountDialog = true) }
            }
            is SettingsEvent.ConfirmDeleteAccount -> {
                viewModelScope.launch {
                    authRepository.deleteAccount()
                    userPreferencesDataStore.clearAuthData()
                    _state.update { it.copy(showDeleteAccountDialog = false, isAuthenticated = false) }
                }
            }
            is SettingsEvent.ClearDataClicked -> {
                _state.update { it.copy(showClearDataDialog = true) }
            }
            is SettingsEvent.ConfirmClearData -> {
                _state.update { it.copy(showClearDataDialog = false) }
                // TODO: Clear Room database and DataStore
            }
            is SettingsEvent.DismissDialog -> {
                _state.update {
                    it.copy(
                        showLogoutDialog = false,
                        showDeleteAccountDialog = false,
                        showClearDataDialog = false,
                        showDailyGoalPicker = false
                    )
                }
            }
        }
    }
}

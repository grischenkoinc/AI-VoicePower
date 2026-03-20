package com.aivoicepower.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.firebase.sync.CloudSyncRepositoryImpl
import com.aivoicepower.data.local.database.AppDatabase
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.repository.AuthRepository
import com.aivoicepower.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val database: AppDatabase,
    private val cloudSyncRepository: CloudSyncRepositoryImpl,
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
                        isReminderEnabled = prefs.isReminderEnabled,
                        isSoundEnabled = prefs.isSoundEnabled,
                        uiVolume = prefs.uiVolume,
                        feedbackVolume = prefs.feedbackVolume,
                        celebrationVolume = prefs.celebrationVolume
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
                val newEnabled = !_state.value.isSoundEnabled
                _state.update { it.copy(isSoundEnabled = newEnabled) }
                viewModelScope.launch {
                    userPreferencesDataStore.setSoundEnabled(newEnabled)
                }
            }
            is SettingsEvent.SetUiVolume -> {
                _state.update { it.copy(uiVolume = event.volume) }
                viewModelScope.launch {
                    userPreferencesDataStore.setUiVolume(event.volume)
                }
            }
            is SettingsEvent.SetFeedbackVolume -> {
                _state.update { it.copy(feedbackVolume = event.volume) }
                viewModelScope.launch {
                    userPreferencesDataStore.setFeedbackVolume(event.volume)
                }
            }
            is SettingsEvent.SetCelebrationVolume -> {
                _state.update { it.copy(celebrationVolume = event.volume) }
                viewModelScope.launch {
                    userPreferencesDataStore.setCelebrationVolume(event.volume)
                }
            }
            is SettingsEvent.ToggleSync -> {
                _state.update { it.copy(isSyncEnabled = !it.isSyncEnabled) }
            }
            is SettingsEvent.LogoutClicked -> {
                _state.update { it.copy(showLogoutDialog = true) }
            }
            is SettingsEvent.ConfirmLogout -> {
                viewModelScope.launch {
                    _state.update { it.copy(showLogoutDialog = false) }
                    authRepository.signOut()
                    _state.update { it.copy(isAuthenticated = false) }
                    // Cleanup in background — fire and forget, doesn't block navigation
                    launch(NonCancellable + Dispatchers.IO) {
                        try {
                            withTimeoutOrNull(10_000L) {
                                cloudSyncRepository.fullSync()
                                cloudSyncRepository.saveUserFlags()
                            }
                        } catch (_: Exception) { }
                        try { userPreferencesDataStore.clearAllUserData() } catch (_: Exception) { }
                        try { database.clearAllTables() } catch (_: Exception) { }
                    }
                }
            }
            is SettingsEvent.DeleteAccountClicked -> {
                _state.update { it.copy(showDeleteAccountDialog = true) }
            }
            is SettingsEvent.ConfirmDeleteAccount -> {
                viewModelScope.launch {
                    _state.update { it.copy(showDeleteAccountDialog = false) }
                    // Best-effort cloud delete with 10s timeout
                    try {
                        withTimeoutOrNull(10_000L) {
                            withContext(Dispatchers.IO) {
                                cloudSyncRepository.deleteAllCloudData()
                            }
                        }
                    } catch (_: Exception) { }
                    authRepository.deleteAccount()
                    withContext(NonCancellable) {
                        _state.update { it.copy(isAuthenticated = false) }
                        userPreferencesDataStore.clearAllUserData()
                        withContext(Dispatchers.IO) {
                            database.clearAllTables()
                        }
                    }
                }
            }
            is SettingsEvent.ClearDataClicked -> {
                _state.update { it.copy(showClearDataDialog = true) }
            }
            is SettingsEvent.ConfirmClearData -> {
                viewModelScope.launch {
                    _state.update { it.copy(showClearDataDialog = false) }
                    // Best-effort cloud delete with 10s timeout
                    try {
                        withTimeoutOrNull(10_000L) {
                            withContext(Dispatchers.IO) {
                                cloudSyncRepository.deleteAllCloudData()
                            }
                        }
                    } catch (_: Exception) { }
                    withContext(Dispatchers.IO) { database.clearAllTables() }
                    userPreferencesDataStore.clearAllUserData()
                    // Keep auth — user stays logged in but restarts onboarding
                    userPreferencesDataStore.setAuthCompleted(true)
                    val user = authRepository.getCurrentUser()
                    user?.uid?.let { userPreferencesDataStore.setFirebaseUid(it) }
                    user?.email?.let { userPreferencesDataStore.setUserEmail(it) }
                    _state.update { it.copy(navigateToOnboarding = true) }
                }
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

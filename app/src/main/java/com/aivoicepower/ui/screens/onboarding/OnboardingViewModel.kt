package com.aivoicepower.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.user.UserGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.PageChanged -> {
                _state.update { it.copy(currentPage = event.page) }
            }

            is OnboardingEvent.GoalSelected -> {
                _state.update { it.copy(selectedGoal = event.goal) }
            }

            is OnboardingEvent.MinutesSelected -> {
                _state.update { it.copy(dailyMinutes = event.minutes) }
            }

            OnboardingEvent.NextClicked -> {
                val currentPage = _state.value.currentPage
                if (currentPage < 3) {
                    _state.update { it.copy(currentPage = currentPage + 1) }
                }
            }

            OnboardingEvent.BackClicked -> {
                val currentPage = _state.value.currentPage
                if (currentPage > 0) {
                    _state.update { it.copy(currentPage = currentPage - 1) }
                }
            }

            OnboardingEvent.StartDiagnosticClicked -> {
                saveOnboardingDataAndNavigate()
            }
        }
    }

    private fun saveOnboardingDataAndNavigate() {
        viewModelScope.launch {
            val currentState = _state.value

            // Зберігаємо вибір користувача в DataStore
            userPreferencesDataStore.setUserGoal(currentState.selectedGoal.name)
            userPreferencesDataStore.setDailyTrainingMinutes(currentState.dailyMinutes)
            userPreferencesDataStore.setOnboardingCompleted(true)

            // Позначаємо що навігація в процесі
            _state.update { it.copy(isNavigating = true) }
        }
    }
}

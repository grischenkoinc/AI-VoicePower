package com.aivoicepower.ui.screens.improvisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImprovisationViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ImprovisationState())
    val state: StateFlow<ImprovisationState> = _state.asStateFlow()

    init {
        loadImprovisationStats()
    }

    fun onEvent(event: ImprovisationEvent) {
        when (event) {
            ImprovisationEvent.RandomTopicClicked -> {
                // Navigation handled in Screen
            }
            ImprovisationEvent.StorytellingClicked -> {
                // Phase 5.2
            }
            ImprovisationEvent.DailyChallengeClicked -> {
                // Phase 5.2
            }
            ImprovisationEvent.DebateClicked -> {
                // Phase 5.3
            }
            ImprovisationEvent.SalesPitchClicked -> {
                // Phase 5.3
            }
            ImprovisationEvent.JobInterviewClicked -> {
                // Navigation handled in Screen
            }
            ImprovisationEvent.PresentationClicked -> {
                // Navigation handled in Screen
            }
            ImprovisationEvent.NegotiationClicked -> {
                // Navigation handled in Screen
            }
        }
    }

    private fun loadImprovisationStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                userPreferencesDataStore.userPreferencesFlow
                    .collect { prefs ->
                        _state.update {
                            it.copy(
                                completedToday = prefs.freeImprovisationsToday,
                                dailyLimit = 3, // FreeTierLimits.FREE_IMPROVISATIONS_PER_DAY
                                isPremium = prefs.isPremium,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалось завантажити дані"
                    )
                }
            }
        }
    }

    fun canStartImprovisation(): Boolean {
        val state = _state.value
        return state.isPremium || state.completedToday < state.dailyLimit
    }
}

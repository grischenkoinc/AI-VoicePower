package com.aivoicepower.ui.screens.username

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserNameState(
    val name: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNavigating: Boolean = false
)

sealed interface UserNameEvent {
    data class NameChanged(val name: String) : UserNameEvent
    data object Continue : UserNameEvent
}

@HiltViewModel
class UserNameViewModel @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(UserNameState())
    val state: StateFlow<UserNameState> = _state.asStateFlow()

    fun onEvent(event: UserNameEvent) {
        when (event) {
            is UserNameEvent.NameChanged -> {
                _state.update { it.copy(name = event.name, error = null) }
            }
            is UserNameEvent.Continue -> {
                saveName()
            }
        }
    }

    private fun saveName() {
        val name = _state.value.name.trim()

        if (name.isBlank()) {
            _state.update { it.copy(error = "Будь ласка, введіть ваше ім'я") }
            return
        }

        if (name.length < 2) {
            _state.update { it.copy(error = "Ім'я має містити принаймні 2 символи") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                userPreferences.setUserName(name)
                _state.update { it.copy(isLoading = false, isNavigating = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Помилка збереження. Спробуйте ще раз"
                    )
                }
            }
        }
    }
}

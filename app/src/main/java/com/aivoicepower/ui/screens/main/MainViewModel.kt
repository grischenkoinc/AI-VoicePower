package com.aivoicepower.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainScreenState(
    val userName: String? = null,
    val userEmail: String? = null,
    val isAuthenticated: Boolean = false,
    val isPremium: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                authRepository.currentUser,
                userPreferencesDataStore.userPreferencesFlow
            ) { authUser, prefs ->
                MainScreenState(
                    userName = authUser?.displayName ?: prefs.userName,
                    userEmail = authUser?.email,
                    isAuthenticated = authUser != null,
                    isPremium = prefs.isPremium
                )
            }.collect { _state.value = it }
        }
    }
}

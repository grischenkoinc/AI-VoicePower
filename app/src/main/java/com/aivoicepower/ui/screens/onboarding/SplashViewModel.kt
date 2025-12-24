package com.aivoicepower.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    val hasCompletedOnboarding: Flow<Boolean> = userPreferencesDataStore.hasCompletedOnboarding
}

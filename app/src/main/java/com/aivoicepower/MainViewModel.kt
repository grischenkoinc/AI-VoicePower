package com.aivoicepower

import androidx.lifecycle.ViewModel
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    val hasCompletedOnboarding: Flow<Boolean> =
        userPreferencesDataStore.userPreferencesFlow.map { prefs ->
            prefs.hasCompletedOnboarding
        }
}

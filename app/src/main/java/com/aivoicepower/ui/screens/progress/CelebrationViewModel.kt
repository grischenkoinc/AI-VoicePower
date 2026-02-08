package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CelebrationViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _currentCelebration = MutableStateFlow<Achievement?>(null)
    val currentCelebration: StateFlow<Achievement?> = _currentCelebration.asStateFlow()

    private val pendingQueue = mutableListOf<Achievement>()

    init {
        viewModelScope.launch {
            achievementRepository.pendingCelebrations.collect { achievement ->
                pendingQueue.add(achievement)
                if (_currentCelebration.value == null) {
                    showNext()
                }
            }
        }
    }

    fun dismiss() {
        pendingQueue.removeFirstOrNull()
        showNext()
    }

    private fun showNext() {
        _currentCelebration.value = pendingQueue.firstOrNull()
    }
}

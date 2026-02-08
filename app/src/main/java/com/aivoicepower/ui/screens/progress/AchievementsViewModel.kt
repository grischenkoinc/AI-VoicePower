package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.model.user.AchievementCategory
import com.aivoicepower.domain.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AchievementsState(
    val isLoading: Boolean = true,
    val achievements: List<Achievement> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = 0
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AchievementsState())
    val state: StateFlow<AchievementsState> = _state.asStateFlow()

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                achievementRepository.getAllAchievements().collect { achievements ->
                    val unlocked = achievements.count { it.isUnlocked }

                    // Sort: unlocked first, then by category order
                    val sorted = achievements.sortedWith(
                        compareBy<Achievement> { it.category.ordinal }
                            .thenByDescending { it.isUnlocked }
                    )

                    _state.update {
                        it.copy(
                            isLoading = false,
                            achievements = sorted,
                            unlockedCount = unlocked,
                            totalCount = achievements.size
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

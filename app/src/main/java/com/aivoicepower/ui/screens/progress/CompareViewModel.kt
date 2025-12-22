package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.user.DiagnosticResult
import com.aivoicepower.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompareState(
    val isLoading: Boolean = true,
    val initialDiagnostic: DiagnosticResult? = null,
    val currentLevel: Int = 0,
    val improvement: Int = 0,
    val comparisons: List<SkillComparison> = emptyList()
)

data class SkillComparison(
    val skillName: String,
    val initialValue: Int,
    val currentValue: Int,
    val improvement: Int
)

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CompareState())
    val state: StateFlow<CompareState> = _state.asStateFlow()

    init {
        loadComparison()
    }

    private fun loadComparison() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Get initial diagnostic
                val diagnostic = userRepository.getInitialDiagnostic().first()

                if (diagnostic == null) {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }

                // Get current progress
                val progress = userRepository.getUserProgress().first()

                if (progress == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            initialDiagnostic = diagnostic
                        )
                    }
                    return@launch
                }

                val comparisons = listOf(
                    SkillComparison(
                        skillName = "Дикцiя",
                        initialValue = diagnostic.diction,
                        currentValue = progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.DICTION] ?: 0,
                        improvement = calculateImprovement(
                            diagnostic.diction,
                            progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.DICTION] ?: 0
                        )
                    ),
                    SkillComparison(
                        skillName = "Темп",
                        initialValue = diagnostic.tempo,
                        currentValue = progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.TEMPO] ?: 0,
                        improvement = calculateImprovement(
                            diagnostic.tempo,
                            progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.TEMPO] ?: 0
                        )
                    ),
                    SkillComparison(
                        skillName = "Iнтонацiя",
                        initialValue = diagnostic.intonation,
                        currentValue = progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.INTONATION] ?: 0,
                        improvement = calculateImprovement(
                            diagnostic.intonation,
                            progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.INTONATION] ?: 0
                        )
                    ),
                    SkillComparison(
                        skillName = "Гучнiсть",
                        initialValue = diagnostic.volume,
                        currentValue = progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.VOLUME] ?: 0,
                        improvement = calculateImprovement(
                            diagnostic.volume,
                            progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.VOLUME] ?: 0
                        )
                    ),
                    SkillComparison(
                        skillName = "Структура",
                        initialValue = diagnostic.structure,
                        currentValue = progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.STRUCTURE] ?: 0,
                        improvement = calculateImprovement(
                            diagnostic.structure,
                            progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.STRUCTURE] ?: 0
                        )
                    ),
                    SkillComparison(
                        skillName = "Впевненiсть",
                        initialValue = diagnostic.confidence,
                        currentValue = progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.CONFIDENCE] ?: 0,
                        improvement = calculateImprovement(
                            diagnostic.confidence,
                            progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.CONFIDENCE] ?: 0
                        )
                    ),
                    SkillComparison(
                        skillName = "Чистота мовлення",
                        initialValue = diagnostic.fillerWords,
                        currentValue = progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.FILLER_WORDS] ?: 0,
                        improvement = calculateImprovement(
                            diagnostic.fillerWords,
                            progress.skillLevels[com.aivoicepower.domain.model.user.SkillType.FILLER_WORDS] ?: 0
                        )
                    )
                )

                val initialOverall = diagnostic.calculateOverallLevel()
                val currentOverall = progress.calculateOverallLevel()
                val overallImprovement = calculateImprovement(initialOverall, currentOverall)

                _state.update {
                    it.copy(
                        isLoading = false,
                        initialDiagnostic = diagnostic,
                        currentLevel = currentOverall,
                        improvement = overallImprovement,
                        comparisons = comparisons
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun calculateImprovement(initial: Int, current: Int): Int {
        if (initial == 0) return 0
        return ((current - initial).toFloat() / initial * 100).toInt()
    }
}

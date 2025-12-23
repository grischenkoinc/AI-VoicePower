package com.aivoicepower.ui.screens.warmup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WarmupViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userProgressDao: UserProgressDao
) : ViewModel() {

    private val _state = MutableStateFlow(WarmupState())
    val state: StateFlow<WarmupState> = _state.asStateFlow()

    init {
        loadWarmupData()
    }

    fun onEvent(event: WarmupEvent) {
        when (event) {
            is WarmupEvent.CategoryClicked -> {
                // Navigation handled in Screen
            }
            WarmupEvent.QuickWarmupClicked -> {
                // Navigation handled in Screen
            }
            WarmupEvent.Refresh -> {
                loadWarmupData()
            }
        }
    }

    private fun loadWarmupData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Завантажуємо статистику та категорії паралельно
                val stats = loadStats()
                val categories = loadCategories()

                _state.update {
                    WarmupState(
                        isLoading = false,
                        stats = stats,
                        categories = categories
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалося завантажити дані"
                    )
                }
            }
        }
    }

    private suspend fun loadStats(): WarmupStats {
        val progress = userProgressDao.getProgress()
        val totalCompletions = warmupCompletionDao.getTotalWarmupDays().firstOrNull() ?: 0

        // Розрахунок хвилин сьогодні
        val today = getCurrentDateString()
        val todayCompletions = warmupCompletionDao.getCompletionsForDate(today)
        val todayMinutes = todayCompletions.sumOf { completion ->
            // Приблизно 1 хвилина на 2 вправи
            (completion.exercisesCompleted + 1) / 2
        }

        return WarmupStats(
            currentStreak = progress?.currentStreak ?: 0,
            todayMinutes = todayMinutes,
            totalCompletions = totalCompletions,
            level = calculateLevel(totalCompletions)
        )
    }

    private suspend fun loadCategories(): List<WarmupCategory> {
        val today = getCurrentDateString()

        // Завантажуємо completion data для кожної категорії
        val articulationCompletion = warmupCompletionDao.getCompletion(today, "articulation")
        val breathingCompletion = warmupCompletionDao.getCompletion(today, "breathing")
        val voiceCompletion = warmupCompletionDao.getCompletion(today, "voice")

        // Завантажуємо останні дати
        val recentCompletions = warmupCompletionDao.getRecentCompletions(30).firstOrNull() ?: emptyList()

        return listOf(
            WarmupCategory(
                id = "articulation",
                icon = "\uD83D\uDC45", // 👅
                title = "Артикуляційна гімнастика",
                exerciseCount = 12,
                estimatedMinutes = 3,
                description = "Розминка м'язів обличчя та язика",
                lastCompletedDate = recentCompletions
                    .lastOrNull { it.category == "articulation" }
                    ?.date,
                completionRate = articulationCompletion?.let {
                    it.exercisesCompleted.toFloat() / it.totalExercises
                } ?: 0f
            ),
            WarmupCategory(
                id = "breathing",
                icon = "\uD83E\uDEC1", // 🫁
                title = "Дихальні вправи",
                exerciseCount = 8,
                estimatedMinutes = 2,
                description = "Розвиток діафрагмального дихання",
                lastCompletedDate = recentCompletions
                    .lastOrNull { it.category == "breathing" }
                    ?.date,
                completionRate = breathingCompletion?.let {
                    it.exercisesCompleted.toFloat() / it.totalExercises
                } ?: 0f
            ),
            WarmupCategory(
                id = "voice",
                icon = "\uD83C\uDFB5", // 🎵
                title = "Розминка голосу",
                exerciseCount = 6,
                estimatedMinutes = 2,
                description = "Вокальні вправи для розігріву",
                lastCompletedDate = recentCompletions
                    .lastOrNull { it.category == "voice" }
                    ?.date,
                completionRate = voiceCompletion?.let {
                    it.exercisesCompleted.toFloat() / it.totalExercises
                } ?: 0f
            )
        )
    }

    private fun calculateLevel(totalCompletions: Int): Int {
        // Простий рівень: кожні 10 розминок = +1 рівень
        return (totalCompletions / 10) + 1
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}

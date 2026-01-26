package com.aivoicepower.ui.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.data.local.database.entity.CourseProgressEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val courseProgressDao: CourseProgressDao
) : ViewModel() {

    fun completeLessonWithScore(courseId: String, lessonId: String, score: Int = 100) {
        viewModelScope.launch {
            val progressId = "${courseId}_${lessonId}"

            // Перевіряємо, чи існує запис
            val existingProgress = courseProgressDao.getLessonProgress(courseId, lessonId)

            if (existingProgress != null) {
                // Оновлюємо існуючий запис
                courseProgressDao.markCompleted(
                    id = progressId,
                    score = score,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                // Створюємо новий запис
                val newProgress = CourseProgressEntity(
                    id = progressId,
                    courseId = courseId,
                    lessonId = lessonId,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis(),
                    bestScore = score,
                    attemptsCount = 1,
                    lastAttemptAt = System.currentTimeMillis()
                )
                courseProgressDao.insertOrUpdate(newProgress)
            }
        }
    }
}

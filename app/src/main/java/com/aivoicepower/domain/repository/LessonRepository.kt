package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.course.Lesson
import kotlinx.coroutines.flow.Flow

// Using old Lesson structure for compatibility - will migrate to NewLesson in future phases
interface LessonRepository {
    fun getAllLessons(): Flow<List<Lesson>>
    suspend fun getLessonById(id: String): Lesson?
    fun getFreeLessons(): Flow<List<Lesson>>
}

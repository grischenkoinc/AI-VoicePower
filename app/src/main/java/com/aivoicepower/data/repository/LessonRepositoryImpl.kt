package com.aivoicepower.data.repository

import com.aivoicepower.data.content.courses.ClearSpeechCourse
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor() : LessonRepository {

    override fun getAllLessons(): Flow<List<Lesson>> {
        return flowOf(ClearSpeechCourse.getLessons())
    }

    override suspend fun getLessonById(id: String): Lesson? {
        return ClearSpeechCourse.getLessons().find { it.id == id }
    }

    override fun getFreeLessons(): Flow<List<Lesson>> {
        // All lessons from ClearSpeechCourse are free
        return flowOf(ClearSpeechCourse.getLessons())
    }
}

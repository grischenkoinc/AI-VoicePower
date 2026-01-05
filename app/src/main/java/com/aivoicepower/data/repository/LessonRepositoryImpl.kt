package com.aivoicepower.data.repository

import com.aivoicepower.data.content.CourseContentProvider
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor() : LessonRepository {

    override fun getAllLessons(): Flow<List<Lesson>> {
        val allLessons = CourseContentProvider.getAllCourses().flatMap { it.lessons }
        return flowOf(allLessons)
    }

    override suspend fun getLessonById(id: String): Lesson? {
        return CourseContentProvider.getAllCourses()
            .flatMap { it.lessons }
            .find { it.id == id }
    }

    override fun getFreeLessons(): Flow<List<Lesson>> {
        // Free lessons from all courses (first 7 lessons of each course)
        val freeLessons = CourseContentProvider.getAllCourses()
            .flatMap { course -> course.lessons.take(7) }
        return flowOf(freeLessons)
    }
}

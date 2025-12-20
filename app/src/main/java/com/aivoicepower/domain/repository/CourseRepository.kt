package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.model.course.LessonProgress
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getAllCourses(): Flow<List<Course>>
    suspend fun getCourseById(id: String): Course?
    suspend fun getLessonById(courseId: String, lessonId: String): Lesson?

    fun getLessonProgress(courseId: String, lessonId: String): Flow<LessonProgress?>
    suspend fun updateLessonProgress(progress: LessonProgress)

    fun getCourseProgress(courseId: String): Flow<List<LessonProgress>>
}

package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.data.content.CourseContentProvider
import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.repository.CourseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CourseRepository using CourseContentProvider
 */
@Singleton
class CourseRepositoryImpl @Inject constructor() : CourseRepository {

    override fun getAllCourses(): Flow<List<Course>> {
        return flowOf(CourseContentProvider.getAllCourses())
    }

    override suspend fun getCourseById(courseId: String): Course? {
        val course = CourseContentProvider.getCourseById(courseId)
        Log.d("CourseRepo", "getCourseById called with: $courseId, found: ${course != null}")
        return course
    }

    override suspend fun getLessonById(lessonId: String): Lesson? {
        return CourseContentProvider.getAllCourses()
            .flatMap { it.lessons }
            .find { it.id == lessonId }
    }

    override fun getLessonById(courseId: String, lessonId: String): Flow<Lesson?> {
        val lesson = CourseContentProvider.getLessonById(courseId, lessonId)
        return flowOf(lesson)
    }

    override fun getLessonsForCourse(courseId: String): Flow<List<Lesson>> {
        val lessons = CourseContentProvider.getCourseById(courseId)?.lessons ?: emptyList()
        return flowOf(lessons)
    }
}

/**
 * Hilt module for CourseRepository
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CourseRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        impl: CourseRepositoryImpl
    ): CourseRepository
}

package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Lesson
import kotlinx.coroutines.flow.Flow

/**
 * Repository for course and lesson data
 *
 * TODO: Implement with real data source in Phase 3-4
 */
interface CourseRepository {
    /**
     * Get all available courses
     */
    fun getAllCourses(): Flow<List<Course>>

    /**
     * Get course by ID
     */
    suspend fun getCourseById(courseId: String): Course?

    /**
     * Get lesson by ID (single result)
     */
    suspend fun getLessonById(lessonId: String): Lesson?

    /**
     * Get lesson by course ID and lesson ID (Flow for reactive updates)
     */
    fun getLessonById(courseId: String, lessonId: String): Flow<Lesson?>

    /**
     * Get all lessons for a course
     */
    fun getLessonsForCourse(courseId: String): Flow<List<Lesson>>
}

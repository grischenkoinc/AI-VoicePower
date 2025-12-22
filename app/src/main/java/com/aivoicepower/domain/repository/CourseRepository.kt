package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Lesson
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    /**
     * Отримати всі доступні курси
     */
    fun getAllCourses(): Flow<List<Course>>

    /**
     * Отримати курс за ID
     */
    fun getCourseById(courseId: String): Flow<Course?>

    /**
     * Отримати урок за ID
     */
    fun getLessonById(courseId: String, lessonId: String): Flow<Lesson?>

    /**
     * Пошук курсів за запитом
     */
    fun searchCourses(query: String): Flow<List<Course>>

    /**
     * Отримати рекомендовані курси на основі цілі користувача
     */
    fun getRecommendedCourses(userGoal: String): Flow<List<Course>>
}

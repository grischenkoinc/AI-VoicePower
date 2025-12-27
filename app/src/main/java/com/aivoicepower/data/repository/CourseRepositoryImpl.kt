package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Difficulty
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.model.user.SkillType
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
 * Stub implementation of CourseRepository
 *
 * TODO Phase 3-4: Implement with CourseContentProvider and Room
 */
@Singleton
class CourseRepositoryImpl @Inject constructor() : CourseRepository {

    override fun getAllCourses(): Flow<List<Course>> {
        return flowOf(getMockCourses())
    }

    override suspend fun getCourseById(courseId: String): Course? {
        val course = getMockCourses().find { it.id == courseId }
        Log.d("CourseRepo", "getCourseById called with: $courseId, found: ${course != null}")
        return course
    }

    override suspend fun getLessonById(lessonId: String): Lesson? {
        return getMockCourses()
            .flatMap { it.lessons }
            .find { it.id == lessonId }
    }

    override fun getLessonById(courseId: String, lessonId: String): Flow<Lesson?> {
        val lesson = getMockCourses()
            .find { it.id == courseId }
            ?.lessons
            ?.find { it.id == lessonId }
        return flowOf(lesson)
    }

    override fun getLessonsForCourse(courseId: String): Flow<List<Lesson>> {
        val lessons = getMockCourses()
            .find { it.id == courseId }
            ?.lessons
            ?: emptyList()
        return flowOf(lessons)
    }

    private fun getMockCourses(): List<Course> {
        return listOf(
            Course(
                id = "basics",
                title = "Основи мовлення",
                description = "Фундаментальні техніки для покращення голосу",
                iconEmoji = "🎤",
                totalLessons = 2,
                isPremium = false,
                estimatedDays = 7,
                difficulty = Difficulty.BEGINNER,
                skills = listOf(SkillType.DICTION, SkillType.VOLUME),
                lessons = listOf(
                    Lesson(
                        id = "lesson_1",
                        courseId = "basics",
                        title = "Дихання та постановка голосу",
                        description = "Основи правильного дихання",
                        order = 1,
                        estimatedMinutes = 30,
                        exercises = emptyList()
                    ),
                    Lesson(
                        id = "lesson_2",
                        courseId = "basics",
                        title = "Артикуляція",
                        description = "Чіткість вимови",
                        order = 2,
                        estimatedMinutes = 30,
                        exercises = emptyList()
                    )
                ),
                estimatedMinutes = 120,
                completedLessons = 0
            ),
            Course(
                id = "advanced",
                title = "Публічні виступи",
                description = "Техніки для впевнених презентацій",
                iconEmoji = "🎯",
                totalLessons = 0,
                isPremium = true,
                estimatedDays = 14,
                difficulty = Difficulty.INTERMEDIATE,
                skills = listOf(SkillType.CONFIDENCE, SkillType.STRUCTURE, SkillType.INTONATION),
                lessons = emptyList(),
                estimatedMinutes = 180,
                completedLessons = 0
            )
        )
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

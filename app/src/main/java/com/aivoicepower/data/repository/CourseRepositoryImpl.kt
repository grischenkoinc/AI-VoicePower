package com.aivoicepower.data.repository

import com.aivoicepower.data.content.CourseContentProvider
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val courseProgressDao: CourseProgressDao
) : CourseRepository {

    override fun getAllCourses(): Flow<List<Course>> = flow {
        val courses = CourseContentProvider.getAllCourses()
        emit(courses)
    }

    override fun getCourseById(courseId: String): Flow<Course?> = flow {
        val course = CourseContentProvider.getCourseById(courseId)
        emit(course)
    }

    override fun getLessonById(courseId: String, lessonId: String): Flow<Lesson?> = flow {
        val lesson = CourseContentProvider.getLessonById(courseId, lessonId)
        emit(lesson)
    }

    override fun searchCourses(query: String): Flow<List<Course>> = flow {
        val allCourses = CourseContentProvider.getAllCourses()
        val filtered = allCourses.filter { course ->
            course.title.contains(query, ignoreCase = true) ||
            course.description.contains(query, ignoreCase = true)
        }
        emit(filtered)
    }

    override fun getRecommendedCourses(userGoal: String): Flow<List<Course>> = flow {
        val allCourses = CourseContentProvider.getAllCourses()

        val recommended = when (userGoal) {
            "CLEAR_SPEECH" -> listOf("course_1", "course_4")
            "PUBLIC_SPEAKING" -> listOf("course_3", "course_6")
            "BETTER_VOICE" -> listOf("course_2", "course_1")
            "PERSUASION" -> listOf("course_5", "course_3")
            "INTERVIEW_PREP" -> listOf("course_5", "course_3")
            else -> listOf("course_1", "course_2")
        }

        val courses = allCourses.filter { it.id in recommended }
        emit(courses)
    }
}

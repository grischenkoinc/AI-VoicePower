package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.CourseProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseProgressDao {

    @Query("SELECT * FROM course_progress WHERE courseId = :courseId ORDER BY lessonId")
    fun getCourseProgress(courseId: String): Flow<List<CourseProgressEntity>>

    @Query("SELECT * FROM course_progress WHERE courseId = :courseId AND lessonId = :lessonId LIMIT 1")
    suspend fun getLessonProgress(courseId: String, lessonId: String): CourseProgressEntity?

    @Query("SELECT * FROM course_progress WHERE courseId = :courseId AND isCompleted = 1")
    fun getCompletedLessons(courseId: String): Flow<List<CourseProgressEntity>>

    @Query("SELECT COUNT(*) FROM course_progress WHERE courseId = :courseId AND isCompleted = 1")
    fun getCompletedLessonsCount(courseId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: CourseProgressEntity)

    @Query("""
        UPDATE course_progress SET
            isCompleted = 1,
            completedAt = :timestamp,
            bestScore = CASE WHEN :score > COALESCE(bestScore, 0) THEN :score ELSE bestScore END,
            attemptsCount = attemptsCount + 1,
            lastAttemptAt = :timestamp
        WHERE id = :id
    """)
    suspend fun markCompleted(id: String, score: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT DISTINCT courseId FROM course_progress WHERE isCompleted = 1")
    fun getStartedCourseIds(): Flow<List<String>>

    @Query("""
        SELECT courseId FROM course_progress
        GROUP BY courseId
        HAVING COUNT(*) = SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END)
    """)
    fun getFullyCompletedCourseIds(): Flow<List<String>>
}

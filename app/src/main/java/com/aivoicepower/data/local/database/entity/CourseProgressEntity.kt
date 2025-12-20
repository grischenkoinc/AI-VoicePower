package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_progress")
data class CourseProgressEntity(
    @PrimaryKey
    val id: String, // Format: "courseId_lessonId"
    val courseId: String,
    val lessonId: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val bestScore: Int? = null,
    val attemptsCount: Int = 0,
    val lastAttemptAt: Long? = null
)

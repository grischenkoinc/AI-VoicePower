package com.aivoicepower.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aivoicepower.data.local.database.dao.*
import com.aivoicepower.data.local.database.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        DiagnosticResultEntity::class,
        UserProgressEntity::class,
        CourseProgressEntity::class,
        RecordingEntity::class,
        AchievementEntity::class,
        WarmupCompletionEntity::class,
        DailyChallengeEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun diagnosticResultDao(): DiagnosticResultDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun courseProgressDao(): CourseProgressDao
    abstract fun recordingDao(): RecordingDao
    abstract fun achievementDao(): AchievementDao
    abstract fun warmupCompletionDao(): WarmupCompletionDao
    abstract fun dailyChallengeDao(): DailyChallengeDao

    companion object {
        const val DATABASE_NAME = "ai_voicepower_db"
    }
}

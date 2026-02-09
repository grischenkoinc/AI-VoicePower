package com.aivoicepower.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aivoicepower.data.local.database.dao.AchievementDao
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.data.local.database.dao.DailyChallengeDao
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.MessageDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.dao.SkillSnapshotDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import com.aivoicepower.data.local.database.entity.AchievementEntity
import com.aivoicepower.data.local.database.entity.CourseProgressEntity
import com.aivoicepower.data.local.database.entity.DailyChallengeEntity
import com.aivoicepower.data.local.database.entity.DiagnosticResultEntity
import com.aivoicepower.data.local.database.entity.MessageEntity
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.database.entity.SkillSnapshotEntity
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity

@Database(
    entities = [
        MessageEntity::class,
        UserProgressEntity::class,
        WarmupCompletionEntity::class,
        DailyChallengeEntity::class,
        RecordingEntity::class,
        CourseProgressEntity::class,
        DiagnosticResultEntity::class,
        SkillSnapshotEntity::class,
        AchievementEntity::class
    ],
    version = 11,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun warmupCompletionDao(): WarmupCompletionDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun recordingDao(): RecordingDao
    abstract fun courseProgressDao(): CourseProgressDao
    abstract fun diagnosticResultDao(): DiagnosticResultDao
    abstract fun skillSnapshotDao(): SkillSnapshotDao
    abstract fun achievementDao(): AchievementDao
}

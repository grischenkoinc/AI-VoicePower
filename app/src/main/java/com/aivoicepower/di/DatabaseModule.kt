package com.aivoicepower.di

import android.content.Context
import androidx.room.Room
import com.aivoicepower.data.local.database.AppDatabase
import com.aivoicepower.data.local.database.dao.AchievementDao
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.data.local.database.dao.DailyChallengeDao
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.MessageDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.dao.SkillSnapshotDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "voicepower_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideUserProgressDao(database: AppDatabase): UserProgressDao {
        return database.userProgressDao()
    }

    @Provides
    @Singleton
    fun provideWarmupCompletionDao(database: AppDatabase): WarmupCompletionDao {
        return database.warmupCompletionDao()
    }

    @Provides
    @Singleton
    fun provideDailyChallengeDao(database: AppDatabase): DailyChallengeDao {
        return database.dailyChallengeDao()
    }

    @Provides
    @Singleton
    fun provideRecordingDao(database: AppDatabase): RecordingDao {
        return database.recordingDao()
    }

    @Provides
    @Singleton
    fun provideCourseProgressDao(database: AppDatabase): CourseProgressDao {
        return database.courseProgressDao()
    }

    @Provides
    @Singleton
    fun provideDiagnosticResultDao(database: AppDatabase): DiagnosticResultDao {
        return database.diagnosticResultDao()
    }

    @Provides
    @Singleton
    fun provideSkillSnapshotDao(database: AppDatabase): SkillSnapshotDao {
        return database.skillSnapshotDao()
    }

    @Provides
    @Singleton
    fun provideAchievementDao(database: AppDatabase): AchievementDao {
        return database.achievementDao()
    }
}

package com.aivoicepower.di

import android.content.Context
import androidx.room.Room
import com.aivoicepower.data.local.database.AppDatabase
import com.aivoicepower.data.local.database.dao.*
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
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // Для розробки; в продакшені використовувати міграції
        .build()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideDiagnosticResultDao(database: AppDatabase): DiagnosticResultDao {
        return database.diagnosticResultDao()
    }

    @Provides
    @Singleton
    fun provideUserProgressDao(database: AppDatabase): UserProgressDao {
        return database.userProgressDao()
    }

    @Provides
    @Singleton
    fun provideCourseProgressDao(database: AppDatabase): CourseProgressDao {
        return database.courseProgressDao()
    }

    @Provides
    @Singleton
    fun provideRecordingDao(database: AppDatabase): RecordingDao {
        return database.recordingDao()
    }

    @Provides
    @Singleton
    fun provideAchievementDao(database: AppDatabase): AchievementDao {
        return database.achievementDao()
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
}

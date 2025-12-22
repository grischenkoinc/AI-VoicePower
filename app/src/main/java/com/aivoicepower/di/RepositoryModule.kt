package com.aivoicepower.di

import com.aivoicepower.data.repository.AchievementRepositoryImpl
import com.aivoicepower.data.repository.CourseRepositoryImpl
import com.aivoicepower.data.repository.UserRepositoryImpl
import com.aivoicepower.domain.repository.AchievementRepository
import com.aivoicepower.domain.repository.CourseRepository
import com.aivoicepower.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        impl: CourseRepositoryImpl
    ): CourseRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: AchievementRepositoryImpl
    ): AchievementRepository
}

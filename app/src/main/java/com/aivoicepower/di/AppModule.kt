package com.aivoicepower.di

import com.aivoicepower.BuildConfig
import com.aivoicepower.data.repository.LessonRepositoryImpl
import com.aivoicepower.data.repository.VoiceAnalysisRepositoryImpl
import com.aivoicepower.domain.repository.LessonRepository
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGeminiModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-2.5-flash-lite",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideLessonRepository(
        impl: LessonRepositoryImpl
    ): LessonRepository = impl

    @Provides
    @Singleton
    fun provideVoiceAnalysisRepository(
        impl: VoiceAnalysisRepositoryImpl
    ): VoiceAnalysisRepository = impl
}

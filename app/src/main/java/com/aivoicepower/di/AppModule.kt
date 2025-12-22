package com.aivoicepower.di

import com.aivoicepower.data.repository.VoiceAnalysisRepositoryImpl
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
        // TODO: Add your Gemini API key here or load from BuildConfig
        return GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = "AIzaSyAte8eIXj8aqTp5-ymLHQqcBZEeuj8miQU" // Replace with actual API key
        )
    }

    @Provides
    @Singleton
    fun provideVoiceAnalysisRepository(
        impl: VoiceAnalysisRepositoryImpl
    ): VoiceAnalysisRepository = impl
}

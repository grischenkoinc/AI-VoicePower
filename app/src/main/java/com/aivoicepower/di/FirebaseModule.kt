package com.aivoicepower.di

import com.aivoicepower.data.firebase.auth.AuthRepositoryImpl
import com.aivoicepower.data.firebase.sync.CloudSyncRepositoryImpl
import com.aivoicepower.domain.repository.AuthRepository
import com.aivoicepower.domain.repository.CloudSyncRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository = impl

    @Provides
    @Singleton
    fun provideCloudSyncRepository(
        impl: CloudSyncRepositoryImpl
    ): CloudSyncRepository = impl
}

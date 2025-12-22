package com.aivoicepower.di

import android.content.Context
import com.aivoicepower.data.billing.BillingClientWrapper
import com.aivoicepower.data.billing.BillingRepository
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingClientWrapper(
        @ApplicationContext context: Context
    ): BillingClientWrapper {
        return BillingClientWrapper(context)
    }

    @Provides
    @Singleton
    fun provideBillingRepository(
        billingClient: BillingClientWrapper,
        userPreferencesDataStore: UserPreferencesDataStore
    ): BillingRepository {
        return BillingRepository(billingClient, userPreferencesDataStore)
    }
}

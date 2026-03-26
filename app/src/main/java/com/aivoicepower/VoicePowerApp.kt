package com.aivoicepower

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.utils.AnalyticsTracker
import com.aivoicepower.utils.NotificationHelper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class VoicePowerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var rewardedAdManager: RewardedAdManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var userPreferencesDataStore: UserPreferencesDataStore

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        // Defer ad SDK init — run on main thread but after first frame
        // Also check Play Services availability to prevent "Something went wrong" dialog on MIUI
        appScope.launch {
            kotlinx.coroutines.delay(1000)
            val playServicesStatus = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this@VoicePowerApp)
            if (playServicesStatus == ConnectionResult.SUCCESS) {
                rewardedAdManager.initialize()
            }
        }
        initAnalyticsUserProperties()
    }

    private fun initAnalyticsUserProperties() {
        appScope.launch(Dispatchers.IO) {
            try {
                val prefs = userPreferencesDataStore.userPreferencesFlow.first()
                val uid = userPreferencesDataStore.firebaseUid.first()
                val accountType = when {
                    uid != null && prefs.userName != null -> "registered"
                    uid != null -> "google"
                    else -> "anonymous"
                }
                analyticsTracker.setUserProperties(
                    isPremium = prefs.isPremium,
                    accountType = accountType
                )
            } catch (_: Exception) {}
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

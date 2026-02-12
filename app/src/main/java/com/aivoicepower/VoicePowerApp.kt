package com.aivoicepower

import android.app.Application
import com.aivoicepower.data.ads.RewardedAdManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class VoicePowerApp : Application() {

    @Inject
    lateinit var rewardedAdManager: RewardedAdManager

    override fun onCreate() {
        super.onCreate()
        rewardedAdManager.initialize()
    }
}

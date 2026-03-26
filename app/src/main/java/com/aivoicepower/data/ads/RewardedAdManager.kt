package com.aivoicepower.data.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.aivoicepower.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.aivoicepower.utils.AnalyticsTracker
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsTracker: AnalyticsTracker
) {
    companion object {
        private const val TAG = "RewardedAdManager"
        private const val REWARDED_AD_UNIT_ID_PROD = "ca-app-pub-8252075318098641/8277139639"
        private const val REWARDED_AD_UNIT_ID_TEST = "ca-app-pub-3940256099942544/5224354917"
        val REWARDED_AD_UNIT_ID: String
            get() = if (BuildConfig.DEBUG) REWARDED_AD_UNIT_ID_TEST else REWARDED_AD_UNIT_ID_PROD
    }

    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false

    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded: StateFlow<Boolean> = _isAdLoaded.asStateFlow()

    private val _isAdLoading = MutableStateFlow(false)
    val isAdLoading: StateFlow<Boolean> = _isAdLoading.asStateFlow()

    fun initialize() {
        if (isInitialized) return
        isInitialized = true
        if (BuildConfig.DEBUG) {
            val testDeviceIds = listOf(
                AdRequest.DEVICE_ID_EMULATOR,
                "9D35ACE46C1BA063E1D292B87C281C72"
            )
            val config = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
            MobileAds.setRequestConfiguration(config)
        }
        MobileAds.initialize(context) {
            Log.d(TAG, "AdMob initialized, debug=${BuildConfig.DEBUG}, adUnitId=$REWARDED_AD_UNIT_ID")
            loadAd()
        }
    }

    fun loadAd() {
        // Lazy init: if AdMob wasn't initialized at startup (Play Services was unavailable),
        // try again now — Play Services may have recovered since then
        if (!isInitialized) {
            val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            if (status == ConnectionResult.SUCCESS) {
                initialize() // initialize() calls loadAd() after MobileAds.init
            }
            return
        }
        if (_isAdLoading.value || _isAdLoaded.value) return

        _isAdLoading.value = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded ad loaded")
                    rewardedAd = ad
                    _isAdLoaded.value = true
                    _isAdLoading.value = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Rewarded ad failed to load: ${error.message}")
                    rewardedAd = null
                    _isAdLoaded.value = false
                    _isAdLoading.value = false
                }
            }
        )
    }

    fun showAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onDismissed: () -> Unit = {},
        onFailed: (String) -> Unit = {}
    ) {
        val ad = rewardedAd
        if (ad == null) {
            onFailed("Реклама ще не завантажена. Спробуйте пізніше.")
            loadAd()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed")
                rewardedAd = null
                _isAdLoaded.value = false
                onDismissed()
                // Preload next ad
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Ad failed to show: ${adError.message}")
                analyticsTracker.logAdWatched("rewarded", completed = false)
                rewardedAd = null
                _isAdLoaded.value = false
                onFailed("Помилка показу реклами: ${adError.message}")
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed")
            }
        }

        ad.show(activity) { rewardItem ->
            Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            analyticsTracker.logAdWatched("rewarded", completed = true)
            onRewarded()
        }
    }
}

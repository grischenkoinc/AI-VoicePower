package com.aivoicepower.data.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsentManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val rewardedAdManager: RewardedAdManager
) {
    companion object {
        private const val TAG = "ConsentManager"
    }

    private val consentInformation = UserMessagingPlatform.getConsentInformation(context)

    val canRequestAds: Boolean get() = consentInformation.canRequestAds()

    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun gatherConsent(activity: Activity) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // Info update success — show form if required
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Consent form error: ${formError.message}")
                    }
                    if (consentInformation.canRequestAds()) {
                        rewardedAdManager.initialize()
                    }
                }
            },
            { error ->
                // Fail open — initialize ads if consent check fails
                Log.w(TAG, "Consent info update failed: ${error.message}")
                rewardedAdManager.initialize()
            }
        )
    }

    fun showPrivacyOptionsForm(activity: Activity, onDismiss: (FormError?) -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onDismiss)
    }
}

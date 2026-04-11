package com.aivoicepower.data.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.aivoicepower.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

private const val BANNER_AD_UNIT_ID_PROD = "ca-app-pub-8252075318098641/1360377497"
private const val BANNER_AD_UNIT_ID_TEST = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = if (BuildConfig.DEBUG) BANNER_AD_UNIT_ID_TEST else BANNER_AD_UNIT_ID_PROD
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

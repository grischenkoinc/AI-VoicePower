package com.aivoicepower

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.aivoicepower.audio.LocalSoundManager
import com.aivoicepower.audio.SoundManager
import com.aivoicepower.data.ads.ConsentManager
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.data.audio.AudioPlayer
import com.aivoicepower.data.billing.BillingClientWrapper
import com.aivoicepower.data.firebase.auth.GoogleSignInHelper
import com.aivoicepower.utils.CloudTtsManager
import com.aivoicepower.ui.navigation.NavGraph
import com.aivoicepower.ui.theme.AIVoicePowerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleSignInHelper: GoogleSignInHelper

    @Inject
    lateinit var rewardedAdManager: RewardedAdManager

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var cloudTtsManager: CloudTtsManager

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var billingClientWrapper: BillingClientWrapper

    @Inject
    lateinit var consentManager: ConsentManager

    override fun attachBaseContext(newBase: Context) {
        // Reset font scale and display size to device defaults
        // Ignores user's "Font size" and "Display size" settings
        val config = Configuration(newBase.resources.configuration)
        config.fontScale = 1f
        config.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gather GDPR consent (UMP SDK) — must be first before ads initialize
        consentManager.gatherConsent(this)

        // Enable edge-to-edge display with transparent system bars
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT
            )
        )

        // Hide navigation bar, show on swipe up
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            CompositionLocalProvider(LocalSoundManager provides soundManager) {
                AIVoicePowerTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent
                    ) {
                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            googleSignInHelper = googleSignInHelper,
                            rewardedAdManager = rewardedAdManager,
                            onShowPrivacyOptions = {
                                consentManager.showPrivacyOptionsForm(this@MainActivity) { }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Reconnect billing client after app restore (prevents Google Play error on Redmi/MIUI)
        billingClientWrapper.reconnect()
    }

    override fun onStop() {
        super.onStop()
        // Stop all audio when app goes to background
        soundManager.stopAll()
        cloudTtsManager.stop()
        audioPlayer.stop()
    }
}

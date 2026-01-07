package com.aivoicepower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aivoicepower.ui.theme.AIVoicePowerTheme
import com.aivoicepower.ui.screens.TestDesignSystemScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIVoicePowerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TestDesignSystemScreen()
                }
            }
        }
    }
}

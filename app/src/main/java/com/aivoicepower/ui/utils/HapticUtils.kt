package com.aivoicepower.ui.utils

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView

/**
 * Performs a light haptic feedback (VIRTUAL_KEY).
 */
fun performHaptic(view: View) {
    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
}

/**
 * Returns a lambda that triggers haptic feedback then calls [onClick].
 */
@Composable
fun hapticClick(onClick: () -> Unit): () -> Unit {
    val view = LocalView.current
    return {
        performHaptic(view)
        onClick()
    }
}

/**
 * Modifier.clickable with built-in haptic feedback.
 */
fun Modifier.hapticClickable(
    view: View,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit
): Modifier = this.clickable(
    interactionSource = interactionSource ?: MutableInteractionSource(),
    indication = null,
    enabled = enabled,
    onClick = {
        performHaptic(view)
        onClick()
    }
)

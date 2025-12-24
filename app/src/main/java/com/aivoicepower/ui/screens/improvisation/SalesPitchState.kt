package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.SalesProductsProvider

data class SalesPitchState(
    val selectedProduct: SalesProductsProvider.SalesProduct? = null,
    val customerProfile: SalesProductsProvider.CustomerProfile? = null,
    val phase: SalesPhase = SalesPhase.ProductSelection,
    val isRecording: Boolean = false,
    val recordingSeconds: Int = 0,
    val maxRecordingSeconds: Int = 90,
    val userPitch: String? = null,
    val customerResponse: String? = null,
    val userObjectionResponse: String? = null,
    val finalDecision: String? = null,
    val isAiThinking: Boolean = false,
    val error: String? = null
)

sealed class SalesPhase {
    object ProductSelection : SalesPhase()
    object CustomerProfile : SalesPhase()
    object OpeningPitch : SalesPhase()
    object CustomerReaction : SalesPhase()
    object HandlingObjection : SalesPhase()
    object FinalDecision : SalesPhase()
    object SalesComplete : SalesPhase()
}

package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.SalesProductsProvider

sealed class SalesPitchEvent {
    data class ProductSelected(val product: SalesProductsProvider.SalesProduct) : SalesPitchEvent()
    object StartPitchClicked : SalesPitchEvent()
    object StartRecordingClicked : SalesPitchEvent()
    object StopRecordingClicked : SalesPitchEvent()
    object ContinueToObjectionClicked : SalesPitchEvent()
    object FinishSalesClicked : SalesPitchEvent()
}

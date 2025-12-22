package com.aivoicepower.data.billing

sealed class BillingState {
    object Idle : BillingState()
    object Connecting : BillingState()
    object Connected : BillingState()
    data class Error(val message: String) : BillingState()
}

sealed class PurchaseResult {
    object Success : PurchaseResult()
    object Cancelled : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
}

package com.aivoicepower.data.billing

import android.app.Activity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    private val billingClient: BillingClientWrapper,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {

    val billingState: StateFlow<BillingState> = billingClient.billingState
    val purchaseResult: StateFlow<PurchaseResult?> = billingClient.purchaseResult
    val availableProducts: StateFlow<List<ProductDetails>> = billingClient.availableProducts
    val isPremiumFromBilling: StateFlow<Boolean> = billingClient.isPremium

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        val product = availableProducts.value.find {
            it.productId == productId
        } ?: return

        val offerToken = if (product.subscriptionOfferDetails != null) {
            product.subscriptionOfferDetails?.firstOrNull()?.offerToken
        } else {
            null
        }

        billingClient.launchBillingFlow(activity, product, offerToken)
    }

    suspend fun handleSuccessfulPurchase() {
        // Update premium status in DataStore
        userPreferencesDataStore.setPremiumStatus(
            isPremium = true,
            expiresAt = null // For subscriptions, Google manages expiration
        )
    }

    fun restorePurchases() {
        billingClient.queryPurchases()
    }

    fun clearPurchaseResult() {
        billingClient.clearPurchaseResult()
    }

    fun reconnect() {
        billingClient.startConnection()
    }

    /**
     * Get product price by ID
     */
    fun getProductPrice(productId: String): String? {
        val product = availableProducts.value.find { it.productId == productId }
        return product?.let {
            if (it.subscriptionOfferDetails != null) {
                // Subscription
                it.subscriptionOfferDetails?.firstOrNull()
                    ?.pricingPhases
                    ?.pricingPhaseList
                    ?.firstOrNull()
                    ?.formattedPrice
            } else {
                // One-time purchase
                it.oneTimePurchaseOfferDetails?.formattedPrice
            }
        }
    }

    /**
     * Check if products are available
     */
    fun areProductsAvailable(): Boolean {
        return availableProducts.value.isNotEmpty()
    }
}

package com.aivoicepower.data.billing

import android.app.Activity
import android.content.Context
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.android.billingclient.api.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingClientWrapper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var billingClient: BillingClient? = null
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 3

    private val _billingState = MutableStateFlow<BillingState>(BillingState.Idle)
    val billingState: StateFlow<BillingState> = _billingState.asStateFlow()

    private val _purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    val purchaseResult: StateFlow<PurchaseResult?> = _purchaseResult.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<ProductDetails>>(emptyList())
    val availableProducts: StateFlow<List<ProductDetails>> = _availableProducts.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    init {
        startConnection()
    }

    fun startConnection() {
        if (billingClient?.isReady == true) return

        // Check Play Services availability first to prevent Google's "Something went wrong"
        // dialog from appearing on MIUI/Redmi devices where Play Services may be restricted
        val playServicesStatus = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context)
        if (playServicesStatus != ConnectionResult.SUCCESS) {
            _billingState.value = BillingState.Error("Google Play Services unavailable ($playServicesStatus)")
            return
        }

        _billingState.value = BillingState.Connecting

        try {
            billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .enablePrepaidPlans()
                        .build()
                )
                .build()

            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        reconnectAttempts = 0
                        _billingState.value = BillingState.Connected
                        queryProducts()
                        queryPurchases()
                    } else {
                        _billingState.value = BillingState.Error(
                            "Billing setup failed: ${billingResult.debugMessage}"
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    _billingState.value = BillingState.Error("Billing service disconnected")
                    // Retry with limit to prevent infinite loop on Redmi/MIUI
                    if (reconnectAttempts < maxReconnectAttempts) {
                        reconnectAttempts++
                        startConnection()
                    }
                }
            })
        } catch (e: Exception) {
            _billingState.value = BillingState.Error("Billing init failed: ${e.message}")
        }
    }

    fun reconnect() {
        reconnectAttempts = 0
        if (billingClient?.isReady != true) {
            startConnection()
        }
    }

    private fun queryProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_monthly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_quarterly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_yearly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _availableProducts.value = productDetailsList
            }
        }
    }

    fun launchBillingFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String? = null
    ) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .apply {
                    offerToken?.let { setOfferToken(it) }
                }
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _purchaseResult.value = PurchaseResult.Cancelled
            }
            else -> {
                _purchaseResult.value = PurchaseResult.Error(
                    billingResult.debugMessage
                )
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            } else {
                grantPremium()
                _purchaseResult.value = PurchaseResult.Success
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                grantPremium()
                _purchaseResult.value = PurchaseResult.Success
            } else {
                _purchaseResult.value = PurchaseResult.Error(
                    "Acknowledge failed: ${billingResult.debugMessage}"
                )
            }
        }
    }

    fun queryPurchases() {
        // Check subscriptions
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchasesList.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (purchase.isAcknowledged) {
                            // Already acknowledged — silently restore premium, no UI notification
                            grantPremium()
                        } else {
                            // Unacknowledged (edge case) — acknowledge and notify
                            handlePurchase(purchase)
                        }
                    }
                }
            }
        }

        // Also check in-app purchases (legacy lifetime)
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchasesList.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (purchase.isAcknowledged) {
                            grantPremium()
                        } else {
                            handlePurchase(purchase)
                        }
                    }
                }
            }
        }
    }

    private fun grantPremium() {
        _isPremium.value = true
        scope.launch {
            userPreferencesDataStore.updatePremiumStatus(true)
        }
    }

    fun clearPurchaseResult() {
        _purchaseResult.value = null
    }

    fun endConnection() {
        billingClient?.endConnection()
    }
}

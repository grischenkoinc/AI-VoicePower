package com.aivoicepower.ui.screens.premium

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.billing.BillingRepository
import com.aivoicepower.data.billing.PurchaseResult
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaywallEvent {
    data class SelectPlan(val plan: PricingPlan) : PaywallEvent()
    object PurchaseClicked : PaywallEvent()
    object RestorePurchases : PaywallEvent()
    object ClosePaywall : PaywallEvent()
    object ClearError : PaywallEvent()
}

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaywallState())
    val state: StateFlow<PaywallState> = _state.asStateFlow()

    init {
        loadPremiumStatus()
        observeBillingState()
    }

    fun onEvent(event: PaywallEvent) {
        when (event) {
            is PaywallEvent.SelectPlan -> selectPlan(event.plan)
            PaywallEvent.PurchaseClicked -> { /* Purchase is handled via launchPurchase */ }
            PaywallEvent.RestorePurchases -> restorePurchases()
            PaywallEvent.ClosePaywall -> { /* Handled by Screen */ }
            PaywallEvent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    fun setSource(source: PaywallSource) {
        _state.update { it.copy(source = source) }
    }

    private fun loadPremiumStatus() {
        viewModelScope.launch {
            userPreferencesDataStore.isPremium.collect { isPremium ->
                _state.update { it.copy(isPremium = isPremium) }
            }
        }
    }

    private fun observeBillingState() {
        viewModelScope.launch {
            billingRepository.purchaseResult.collect { result ->
                when (result) {
                    is PurchaseResult.Success -> {
                        billingRepository.handleSuccessfulPurchase()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isPremium = true
                            )
                        }
                        billingRepository.clearPurchaseResult()
                    }
                    is PurchaseResult.Cancelled -> {
                        _state.update { it.copy(isLoading = false) }
                        billingRepository.clearPurchaseResult()
                    }
                    is PurchaseResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        billingRepository.clearPurchaseResult()
                    }
                    null -> { /* Do nothing */ }
                }
            }
        }

        // Also observe billing premium status
        viewModelScope.launch {
            billingRepository.isPremiumFromBilling.collect { isPremium ->
                if (isPremium) {
                    billingRepository.handleSuccessfulPurchase()
                    _state.update { it.copy(isPremium = true) }
                }
            }
        }
    }

    private fun selectPlan(plan: PricingPlan) {
        _state.update { it.copy(selectedPlan = plan) }
    }

    fun launchPurchase(activity: Activity) {
        _state.update { it.copy(isLoading = true) }
        val productId = _state.value.selectedPlan.productId

        if (billingRepository.areProductsAvailable()) {
            billingRepository.launchPurchaseFlow(activity, productId)
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Продукти недоступні. Спробуйте пізніше."
                )
            }
        }
    }

    private fun restorePurchases() {
        _state.update { it.copy(isLoading = true) }
        billingRepository.restorePurchases()

        // Set a timeout for restore
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            if (_state.value.isLoading) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = if (!_state.value.isPremium) {
                            "Покупки не знайдено"
                        } else null
                    )
                }
            }
        }
    }
}

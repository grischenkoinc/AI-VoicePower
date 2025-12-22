package com.aivoicepower.ui.screens.premium

data class PaywallState(
    val isPremium: Boolean = false,
    val selectedPlan: PricingPlan = PricingPlan.YEARLY,
    val isLoading: Boolean = false,
    val error: String? = null,
    val source: PaywallSource = PaywallSource.UNKNOWN
)

enum class PricingPlan(
    val productId: String,
    val price: String,
    val duration: String,
    val savings: String? = null,
    val isPopular: Boolean = false
) {
    MONTHLY(
        productId = "premium_monthly",
        price = "$9.99",
        duration = "на місяць",
        savings = null,
        isPopular = false
    ),
    YEARLY(
        productId = "premium_yearly",
        price = "$59.99",
        duration = "на рік",
        savings = "Економія 50%",
        isPopular = true
    ),
    LIFETIME(
        productId = "premium_lifetime",
        price = "$149.99",
        duration = "назавжди",
        savings = "Найвигідніше",
        isPopular = false
    )
}

enum class PaywallSource {
    UNKNOWN,
    COURSE_LOCKED,
    IMPROV_LIMIT,
    AI_COACH_LIMIT,
    DIAGNOSTIC_LIMIT,
    SETTINGS,
    ACHIEVEMENT
}

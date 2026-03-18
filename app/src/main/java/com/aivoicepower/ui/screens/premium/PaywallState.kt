package com.aivoicepower.ui.screens.premium

data class PaywallState(
    val isPremium: Boolean = false,
    val selectedPlan: PricingPlan = PricingPlan.QUARTERLY,
    val isLoading: Boolean = false,
    val error: String? = null,
    val source: PaywallSource = PaywallSource.UNKNOWN
)

enum class PricingPlan(
    val productId: String,
    val price: String,
    val duration: String,
    val perMonth: String? = null,
    val savings: String? = null,
    val isPopular: Boolean = false
) {
    MONTHLY(
        productId = "premium_monthly",
        price = "$14.99",
        duration = "на місяць"
    ),
    QUARTERLY(
        productId = "premium_quarterly",
        price = "$39.99",
        duration = "на 3 місяці",
        perMonth = "$13.33/міс",
        savings = "Економія 11%",
        isPopular = true
    ),
    YEARLY(
        productId = "premium_yearly",
        price = "$99.99",
        duration = "на рік",
        perMonth = "$8.33/міс",
        savings = "Економія 44%"
    )
}

enum class PaywallSource {
    UNKNOWN,
    COURSE_LOCKED,
    IMPROV_LIMIT,
    AI_COACH_LIMIT,
    DIAGNOSTIC_LIMIT,
    ANALYSIS_LIMIT,
    IMPROV_ANALYSIS_LIMIT,
    SETTINGS,
    ACHIEVEMENT
}

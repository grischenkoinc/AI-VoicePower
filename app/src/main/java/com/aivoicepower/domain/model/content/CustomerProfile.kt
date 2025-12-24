package com.aivoicepower.domain.model.content

data class CustomerProfile(
    val type: String,        // "зайнятий бізнесмен", "скептик", "обережний покупець"
    val characteristics: List<String>,
    val objections: List<String>,
    val interests: List<String>
)

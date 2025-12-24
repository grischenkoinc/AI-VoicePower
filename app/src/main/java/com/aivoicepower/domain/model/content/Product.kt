package com.aivoicepower.domain.model.content

data class Product(
    val name: String,
    val description: String,
    val price: String?,
    val benefits: List<String>,
    val isAbsurd: Boolean = false    // Для креативної практики
)

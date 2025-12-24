package com.aivoicepower.domain.model.exercise

data class DialogueLine(
    val speaker: String,
    val text: String,
    val emotion: Emotion? = null,
    val isUserLine: Boolean // true = користувач читає цю репліку
)

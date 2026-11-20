package com.mindguard.domain.model

data class ProtectionPolicy(
    val id: String,
    val dailyLimitMinutes: Int = 60,
    val isEnabled: Boolean = true
)

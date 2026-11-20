package com.mindguard.domain.model

data class UsageRecord(
    val id: String,
    val appName: String,
    val usageTimeMinutes: Int
)

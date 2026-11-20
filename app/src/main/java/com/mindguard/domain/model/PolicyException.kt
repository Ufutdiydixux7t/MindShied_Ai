package com.mindguard.domain.model

data class PolicyException(
    val id: String,
    val appName: String,
    val extraMinutesAllowed: Int
)

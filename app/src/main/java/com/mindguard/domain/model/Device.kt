package com.mindguard.domain.model

data class Device(
    val id: String,
    val name: String,
    val isProtected: Boolean = true
)

package com.mindguard.domain.model

data class ChildProfile(
    val id: String,
    val name: String,
    val age: Int,
    val devices: List<Device> = emptyList()
)

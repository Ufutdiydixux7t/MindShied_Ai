package com.mindguard.domain.model

data class Family(
    val id: String,
    val name: String,
    val members: List<FamilyMember> = emptyList()
)

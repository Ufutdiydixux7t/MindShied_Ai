package com.mindguard.domain.repository

import com.mindguard.domain.model.ProtectionPolicy

interface ProtectionRepository {
    suspend fun getPolicy(): ProtectionPolicy?
}

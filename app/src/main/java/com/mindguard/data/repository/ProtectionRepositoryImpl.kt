package com.mindguard.data.repository

import com.mindguard.domain.model.ProtectionPolicy
import com.mindguard.domain.repository.ProtectionRepository

class ProtectionRepositoryImpl : ProtectionRepository {
    override suspend fun getPolicy(): ProtectionPolicy? = null
}

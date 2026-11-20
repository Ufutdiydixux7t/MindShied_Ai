package com.mindguard.data.repository

import com.mindguard.domain.model.UsageRecord
import com.mindguard.domain.repository.UsageRepository

class UsageRepositoryImpl : UsageRepository {
    override suspend fun getUsageRecords(): List<UsageRecord> = emptyList()
}

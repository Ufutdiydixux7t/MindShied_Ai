package com.mindguard.domain.repository

import com.mindguard.domain.model.UsageRecord

interface UsageRepository {
    suspend fun getUsageRecords(): List<UsageRecord>
}

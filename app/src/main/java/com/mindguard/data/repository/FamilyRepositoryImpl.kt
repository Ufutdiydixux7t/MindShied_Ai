package com.mindguard.data.repository

import com.mindguard.domain.model.Family
import com.mindguard.domain.repository.FamilyRepository

class FamilyRepositoryImpl : FamilyRepository {
    override suspend fun getFamily(): Family? = null
}

package com.mindguard.domain.repository

import com.mindguard.domain.model.Family

interface FamilyRepository {
    suspend fun getFamily(): Family?
}

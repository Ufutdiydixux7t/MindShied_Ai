package com.mindguard.data.repository

import com.mindguard.domain.model.PolicyException
import com.mindguard.domain.repository.ExceptionRepository

class ExceptionRepositoryImpl : ExceptionRepository {
    override suspend fun getExceptions(): List<PolicyException> = emptyList()
}

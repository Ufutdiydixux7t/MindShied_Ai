package com.mindguard.domain.repository

import com.mindguard.domain.model.PolicyException

interface ExceptionRepository {
    suspend fun getExceptions(): List<PolicyException>
}

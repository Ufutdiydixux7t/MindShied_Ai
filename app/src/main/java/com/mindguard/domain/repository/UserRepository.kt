package com.mindguard.domain.repository

import com.mindguard.domain.model.User

interface UserRepository {
    suspend fun getUser(): User?
}

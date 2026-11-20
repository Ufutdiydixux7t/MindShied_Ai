package com.mindguard.data.repository

import com.mindguard.domain.model.User
import com.mindguard.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    override suspend fun getUser(): User? = null
}

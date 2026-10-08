package com.example.chitassist.domain.repository

import com.example.chitassist.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerUser(user: User): Result<Long>
    suspend fun getUserByMobile(mobileNumber: String): User?
    fun getUserById(id: Long): Flow<User?>
    fun getLoggedInUser(): Flow<User?>
    suspend fun saveSession(userId: Long)
    suspend fun clearSession()
    fun isLoggedIn(): Flow<Boolean>
    suspend fun updateUser(user: User): Result<Unit>
}

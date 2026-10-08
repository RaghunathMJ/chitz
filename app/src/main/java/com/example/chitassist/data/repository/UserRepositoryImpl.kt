package com.example.chitassist.data.repository

import com.example.chitassist.core.database.dao.UserDao
import com.example.chitassist.core.datastore.SessionDataStore
import com.example.chitassist.data.mapper.toDomain
import com.example.chitassist.data.mapper.toEntity
import com.example.chitassist.domain.model.User
import com.example.chitassist.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val sessionDataStore: SessionDataStore
) : UserRepository {

    override suspend fun registerUser(user: User): Result<Long> {
        return try {
            val id = userDao.insertUser(user.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByMobile(mobileNumber: String): User? {
        return userDao.getUserByMobile(mobileNumber)?.toDomain()
    }

    override fun getUserById(id: Long): Flow<User?> {
        return userDao.getUserById(id).map { it?.toDomain() }
    }

    override fun getLoggedInUser(): Flow<User?> {
        return sessionDataStore.userId.flatMapLatest { userId ->
            if (userId != null && userId != -1L) {
                userDao.getUserById(userId).map { it?.toDomain() }
            } else {
                flowOf(null)
            }
        }
    }

    override suspend fun saveSession(userId: Long) {
        sessionDataStore.saveSession(userId)
    }

    override suspend fun clearSession() {
        sessionDataStore.clearSession()
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return sessionDataStore.isLoggedIn
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            userDao.updateUser(user.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

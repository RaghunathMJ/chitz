package com.example.chitassist.domain.repository

import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface ChitRepository {
    suspend fun insertChit(chit: Chit): Long
    suspend fun updateChit(chit: Chit)
    suspend fun deleteChit(chit: Chit)
    fun getChitById(id: Long): Flow<Chit?>
    fun getChitsByUserId(userId: Long): Flow<List<Chit>>

    suspend fun insertPayment(payment: Payment): Long
    suspend fun updatePayment(payment: Payment)
    suspend fun syncChitProgress(chitId: Long)
    fun getPaymentsForChit(chitId: Long): Flow<List<Payment>>
    fun getUpcomingPayments(userId: Long, limit: Int = 3): Flow<List<Payment>>
}

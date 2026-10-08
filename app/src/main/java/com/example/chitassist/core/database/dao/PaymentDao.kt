package com.example.chitassist.core.database.dao

import androidx.room.*
import com.example.chitassist.core.database.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Query("SELECT * FROM payments WHERE chitId = :chitId ORDER BY installmentNumber ASC")
    fun getPaymentsForChit(chitId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE chitId = :chitId ORDER BY installmentNumber ASC")
    suspend fun getPaymentsForChitSync(chitId: Long): List<PaymentEntity>

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("SELECT COUNT(*) FROM payments WHERE chitId = :chitId AND status = 'PAID'")
    suspend fun getPaidInstallmentsCount(chitId: Long): Int

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Query("""
        SELECT p.* FROM payments p
        INNER JOIN chits c ON p.chitId = c.id
        WHERE c.userId = :userId AND p.status = 'PENDING'
        ORDER BY p.dueDate ASC
        LIMIT :limit
    """)
    fun getUpcomingPayments(userId: Long, limit: Int): Flow<List<PaymentEntity>>
}

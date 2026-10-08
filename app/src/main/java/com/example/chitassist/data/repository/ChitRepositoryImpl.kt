package com.example.chitassist.data.repository

import com.example.chitassist.core.database.dao.ChitDao
import com.example.chitassist.core.database.dao.PaymentDao
import com.example.chitassist.data.mapper.toDomain
import com.example.chitassist.data.mapper.toEntity
import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.model.ChitStatus
import com.example.chitassist.domain.model.Payment
import com.example.chitassist.domain.model.PaymentStatus
import com.example.chitassist.domain.model.DividendCalculator
import com.example.chitassist.domain.model.DividendMode
import com.example.chitassist.domain.repository.ChitRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChitRepositoryImpl @Inject constructor(
    private val chitDao: ChitDao,
    private val paymentDao: PaymentDao
) : ChitRepository {

    override suspend fun insertChit(chit: Chit): Long {
        return chitDao.insertChit(chit.toEntity())
    }

    override suspend fun updateChit(chit: Chit) {
        chitDao.updateChit(chit.toEntity())
        syncChitProgress(chit.id)
    }

    override suspend fun deleteChit(chit: Chit) {
        chitDao.deleteChit(chit.toEntity())
    }

    override fun getChitById(id: Long): Flow<Chit?> {
        return chitDao.getChitById(id).map { it?.toDomain() }
    }

    override fun getChitsByUserId(userId: Long): Flow<List<Chit>> {
        return chitDao.getChitsForUser(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertPayment(payment: Payment): Long {
        return paymentDao.insertPayment(payment.toEntity())
    }

    override suspend fun updatePayment(payment: Payment) {
        paymentDao.updatePayment(payment.toEntity())
        syncChitProgress(payment.chitId)
    }

    override suspend fun syncChitProgress(chitId: Long) {
        val paymentsEntities = paymentDao.getPaymentsForChitSync(chitId)
        val payments = paymentsEntities.map { it.toDomain() }
        
        val paidCount = payments.count { it.status == PaymentStatus.PAID }
        val winningPayment = payments.find { it.isWinner }
        
        val chitEntity = chitDao.getChitByIdSync(chitId)
        val chit = chitEntity?.toDomain() ?: return

        // Robust status transition logic: If all installments are paid, it MUST be COMPLETED.
        val totalExpected = chit.numberOfInstallments
        val isAllPaid = paidCount >= totalExpected && totalExpected > 0
        
        val targetStatus = when {
            isAllPaid -> ChitStatus.COMPLETED
            !isAllPaid && chit.status == ChitStatus.COMPLETED -> ChitStatus.ACTIVE
            else -> chit.status
        }
        
        val updatedChit = chit.copy(
            completedInstallments = paidCount,
            isWon = winningPayment != null,
            winningInstallmentNumber = winningPayment?.installmentNumber,
            status = targetStatus,
            updatedAt = System.currentTimeMillis()
        )
        
        // Perform update to ensure database consistency
        chitDao.updateChit(updatedChit.toEntity())
        
        // Recalculate dividends for all payments to ensure consistency
        payments.forEach { payment ->
            val newDividend = calculateDividendForUser(payment, updatedChit, payments)
            if (Math.abs(newDividend - payment.dividend) > 0.01) {
                paymentDao.updatePayment(payment.copy(dividend = newDividend).toEntity())
            }
        }
    }

    private fun calculateDividendForUser(
        payment: Payment,
        chit: Chit,
        allPayments: List<Payment>
    ): Double {
        if (payment.bidAmount <= 0) return 0.0
        
        val isEligible = when (chit.dividendMode) {
            DividendMode.INCLUDING_WINNER -> true
            DividendMode.EXCLUDING_WINNER -> {
                // User is eligible if they haven't won yet in this or any previous installment
                val winnerPayment = allPayments.find { it.isWinner }
                if (winnerPayment == null) {
                    true // No winner yet, everyone is eligible
                } else {
                    payment.installmentNumber < winnerPayment.installmentNumber
                }
            }
        }
        
        if (!isEligible) return 0.0
        
        val discount = (chit.totalValue - payment.bidAmount).coerceAtLeast(0.0) - chit.commissionAmount
        
        return DividendCalculator.calculateDividend(
            totalMembers = chit.numberOfInstallments,
            installmentNumber = payment.installmentNumber,
            discount = discount,
            mode = chit.dividendMode
        )
    }

    override fun getPaymentsForChit(chitId: Long): Flow<List<Payment>> {
        return paymentDao.getPaymentsForChit(chitId).map { list -> list.map { it.toDomain() } }
    }

    override fun getUpcomingPayments(userId: Long, limit: Int): Flow<List<Payment>> {
        return paymentDao.getUpcomingPayments(userId, limit).map { list -> list.map { it.toDomain() } }
    }
}

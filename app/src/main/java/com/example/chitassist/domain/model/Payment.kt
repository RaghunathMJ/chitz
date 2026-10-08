package com.example.chitassist.domain.model

data class Payment(
    val id: Long = 0,
    val chitId: Long,
    val chitName: String = "",
    val installmentNumber: Int,
    val dueDate: Long,
    val amount: Double,
    val dividend: Double = 0.0,
    val bidAmount: Double = 0.0,
    val isWinner: Boolean = false,
    val paidDate: Long? = null,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val notes: String? = null
) {
    val netAmount: Double get() = amount - dividend
}

enum class PaymentStatus {
    PENDING, PAID, OVERDUE
}

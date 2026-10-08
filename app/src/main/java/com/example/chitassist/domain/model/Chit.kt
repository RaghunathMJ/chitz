package com.example.chitassist.domain.model


data class Chit(
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val organizer: String? = null,
    val chitNumber: String? = null,
    val description: String? = null,
    val totalValue: Double,
    val monthlyContribution: Double,
    val numberOfInstallments: Int,
    val startDate: Long,
    val paymentDueDay: Int,
    val intervalMonths: Int = 1,
    val status: ChitStatus = ChitStatus.ACTIVE,
    val dividendMode: DividendMode = DividendMode.EXCLUDING_WINNER,
    val commissionAmount: Double = 0.0,
    val completedInstallments: Int = 0,
    val isWon: Boolean = false,
    val winningInstallmentNumber: Int? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DividendMode {
    EXCLUDING_WINNER, // Distributed among members excluding the winner and foreman
    INCLUDING_WINNER  // Distributed among all members including the winner
}

enum class Frequency {
    MONTHLY, WEEKLY, CUSTOM
}

enum class ChitStatus {
    ACTIVE, COMPLETED, CANCELLED
}

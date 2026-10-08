package com.example.chitassist.domain.model

object DividendCalculator {
    /**
     * Calculates the dividend per eligible member.
     * 
     * @param totalMembers Total number of members in the chit fund (usually equal to number of installments).
     * @param installmentNumber The current installment number (1-based).
     * @param discount The total discount (Chit Amount - Bid Amount - Commission).
     * @param mode The dividend distribution mode.
     * @return The dividend amount for each eligible member.
     */
    fun calculateDividend(
        totalMembers: Int,
        installmentNumber: Int,
        discount: Double,
        mode: DividendMode
    ): Double {
        if (discount <= 0 || totalMembers <= 0) return 0.0

        val divisor = when (mode) {
            DividendMode.EXCLUDING_WINNER -> {
                // In excluding winner mode, the dividend is shared among members
                // who have not yet won. At installment N, the current winner
                // and previous winners (N-1) don't get the dividend.
                // So (totalMembers - installmentNumber) members get it.
                (totalMembers - installmentNumber).coerceAtLeast(1)
            }
            DividendMode.INCLUDING_WINNER -> {
                // Everyone receives a share of the dividend regardless of when they win.
                totalMembers
            }
        }

        return discount / divisor
    }
}

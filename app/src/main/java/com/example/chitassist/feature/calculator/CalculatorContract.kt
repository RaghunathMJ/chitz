package com.example.chitassist.feature.calculator

import com.example.chitassist.domain.model.DividendMode

class CalculatorContract {
    enum class BidType {
        TOTAL_BID,      // The amount winner receives (e.g., 80,000)
        BID_DISCOUNT    // The amount reduced from total (e.g., 20,000)
    }

    data class State(
        val chitAmount: String = "",
        val months: String = "",
        val installmentNumber: String = "1",
        val commissionAmount: String = "0",
        val bidAmount: String = "",
        val bidType: BidType = BidType.TOTAL_BID,
        val dividendMode: DividendMode = DividendMode.EXCLUDING_WINNER,
        val monthlyInstallment: Double = 0.0,
        val totalCommission: Double = 0.0,
        val dividend: Double = 0.0,
        val actualPayment: Double = 0.0,
        val effectiveMonthlyInstallment: Double = 0.0
    )

    sealed class Intent {
        data class ChitAmountChanged(val amount: String) : Intent()
        data class MonthsChanged(val months: String) : Intent()
        data class InstallmentNumberChanged(val number: String) : Intent()
        data class CommissionAmountChanged(val amount: String) : Intent()
        data class BidAmountChanged(val amount: String) : Intent()
        data class BidTypeChanged(val type: BidType) : Intent()
        data class DividendModeChanged(val mode: DividendMode) : Intent()
        object Calculate : Intent()
    }

    sealed class Effect {
        data class ShowError(val message: String) : Effect()
    }
}

package com.example.chitassist.feature.chits.add

import com.example.chitassist.domain.model.DividendMode

class AddChitContract {
    data class State(
        val name: String = "",
        val organizer: String = "",
        val totalValue: String = "",
        val monthlyContribution: String = "",
        val numberOfInstallments: String = "",
        val commissionAmount: String = "",
        val startDate: Long = System.currentTimeMillis(),
        val paymentDueDay: String = "1",
        val intervalMonths: Int = 1,
        val dividendMode: DividendMode = DividendMode.EXCLUDING_WINNER,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Intent {
        data class LoadChit(val chitId: Long) : Intent()
        data class OnNameChanged(val name: String) : Intent()
        data class OnOrganizerChanged(val organizer: String) : Intent()
        data class OnTotalValueChanged(val value: String) : Intent()
        data class OnMonthlyContributionChanged(val value: String) : Intent()
        data class OnInstallmentsChanged(val value: String) : Intent()
        data class OnCommissionAmountChanged(val value: String) : Intent()
        data class OnDueDateChanged(val day: String) : Intent()
        data class OnStartDateChanged(val date: Long) : Intent()
        data class OnIntervalChanged(val interval: Int) : Intent()
        data class OnDividendModeChanged(val mode: DividendMode) : Intent()
        object OnSaveClicked : Intent()
    }

    sealed class Effect {
        object NavigationBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}

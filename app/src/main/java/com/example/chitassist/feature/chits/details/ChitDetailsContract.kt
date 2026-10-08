package com.example.chitassist.feature.chits.details

import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.model.Payment

class ChitDetailsContract {
    data class State(
        val chit: Chit? = null,
        val payments: List<Payment> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Intent {
        data class LoadChitDetails(val chitId: Long) : Intent()
        data class OnPaymentStatusChanged(val payment: Payment, val isPaid: Boolean, val bidAmount: String) : Intent()
        data class OnWinnerStatusChanged(val payment: Payment, val isWinner: Boolean) : Intent()
    }

    sealed class Effect {
        data class ShowError(val message: String) : Effect()
    }
}

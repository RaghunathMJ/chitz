package com.example.chitassist.feature.chits.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitassist.domain.model.PaymentStatus
import com.example.chitassist.domain.repository.ChitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChitDetailsViewModel @Inject constructor(
    private val chitRepository: ChitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chitId: Long = checkNotNull(savedStateHandle["chitId"])

    private val _state = MutableStateFlow(ChitDetailsContract.State())
    val state: StateFlow<ChitDetailsContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ChitDetailsContract.Effect>()
    val effect = _effect.asSharedFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Ensure status and progress are synced when loading
            chitRepository.syncChitProgress(chitId)
            
            combine(
                chitRepository.getChitById(chitId),
                chitRepository.getPaymentsForChit(chitId)
            ) { chit, payments ->
                Triple(chit, payments, Unit)
            }.collect { (chit, payments, _) ->
                _state.update { 
                    it.copy(
                        chit = chit,
                        payments = payments,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onIntent(intent: ChitDetailsContract.Intent) {
        when (intent) {
            is ChitDetailsContract.Intent.LoadChitDetails -> loadDetails()
            is ChitDetailsContract.Intent.OnPaymentStatusChanged -> {
                updatePaymentStatus(intent.payment, intent.isPaid, intent.bidAmount)
            }
            is ChitDetailsContract.Intent.OnWinnerStatusChanged -> {
                updateWinnerStatus(intent.payment, intent.isWinner)
            }
        }
    }

    private fun updateWinnerStatus(payment: com.example.chitassist.domain.model.Payment, isWinner: Boolean) {
        // Ensure only one winner
        if (isWinner && _state.value.payments.any { it.isWinner && it.id != payment.id }) {
            return
        }

        viewModelScope.launch {
            val updatedPayment = payment.copy(
                isWinner = isWinner,
                dividend = 0.0 // dividend will be recalculated in syncChitProgress
            )
            chitRepository.updatePayment(updatedPayment)
        }
    }

    private fun updatePaymentStatus(
        payment: com.example.chitassist.domain.model.Payment,
        isPaid: Boolean,
        bidAmountStr: String
    ) {
        viewModelScope.launch {
            if (isPaid) {
                // Validation 1: Check if previous installment is paid
                val allPayments = _state.value.payments.sortedBy { it.installmentNumber }
                val currentIndex = allPayments.indexOfFirst { it.id == payment.id }
                if (currentIndex > 0) {
                    val previousPayment = allPayments[currentIndex - 1]
                    if (previousPayment.status != PaymentStatus.PAID) {
                        _effect.emit(ChitDetailsContract.Effect.ShowError("Please pay installment #${previousPayment.installmentNumber} first."))
                        return@launch
                    }
                }

                // Validation 2: Bid amount check
                val bidAmount = bidAmountStr.replace(",", "").toDoubleOrNull() ?: 0.0
                if (bidAmount <= 0) {
                    _effect.emit(ChitDetailsContract.Effect.ShowError("Please enter a valid Winning Bid Amount."))
                    return@launch
                }

                val updatedPayment = payment.copy(
                    status = PaymentStatus.PAID,
                    bidAmount = bidAmount,
                    paidDate = System.currentTimeMillis()
                )
                chitRepository.updatePayment(updatedPayment)
            } else {
                // When unmarking as paid, also check if subsequent installments are paid
                val allPayments = _state.value.payments.sortedBy { it.installmentNumber }
                val currentIndex = allPayments.indexOfFirst { it.id == payment.id }
                if (currentIndex < allPayments.size - 1) {
                    val nextPayment = allPayments[currentIndex + 1]
                    if (nextPayment.status == PaymentStatus.PAID) {
                        _effect.emit(ChitDetailsContract.Effect.ShowError("Cannot unmark as paid because installment #${nextPayment.installmentNumber} is already paid."))
                        return@launch
                    }
                }

                val updatedPayment = payment.copy(
                    status = PaymentStatus.PENDING,
                    paidDate = null
                )
                chitRepository.updatePayment(updatedPayment)
            }
        }
    }
}

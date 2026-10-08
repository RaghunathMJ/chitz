package com.example.chitassist.feature.calculator

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    private val _viewState = MutableStateFlow(CalculatorContract.State())
    val viewState: StateFlow<CalculatorContract.State> = _viewState.asStateFlow()

    private val _effect = MutableSharedFlow<CalculatorContract.Effect>()
    val effect: SharedFlow<CalculatorContract.Effect> = _effect.asSharedFlow()

    fun handleIntent(intent: CalculatorContract.Intent) {
        when (intent) {
            is CalculatorContract.Intent.ChitAmountChanged -> {
                _viewState.update { it.copy(chitAmount = formatInput(intent.amount)) }
                calculate()
            }
            is CalculatorContract.Intent.MonthsChanged -> {
                _viewState.update { it.copy(months = intent.months) }
                calculate()
            }
            is CalculatorContract.Intent.InstallmentNumberChanged -> {
                _viewState.update { it.copy(installmentNumber = intent.number) }
                calculate()
            }
            is CalculatorContract.Intent.CommissionAmountChanged -> {
                _viewState.update { it.copy(commissionAmount = formatInput(intent.amount)) }
                calculate()
            }
            is CalculatorContract.Intent.BidAmountChanged -> {
                _viewState.update { it.copy(bidAmount = formatInput(intent.amount)) }
                calculate()
            }
            is CalculatorContract.Intent.BidTypeChanged -> {
                _viewState.update { it.copy(bidType = intent.type) }
                calculate()
            }
            is CalculatorContract.Intent.DividendModeChanged -> {
                _viewState.update { it.copy(dividendMode = intent.mode) }
                calculate()
            }
            CalculatorContract.Intent.Calculate -> calculate()
        }
    }

    private fun calculate() {
        val state = _viewState.value
        val amount = state.chitAmount.replace(",", "").toDoubleOrNull() ?: 0.0
        val months = state.months.toIntOrNull() ?: 0
        val installmentNumber = state.installmentNumber.toIntOrNull() ?: 1
        val commissionAmount = state.commissionAmount.replace(",", "").toDoubleOrNull() ?: 0.0
        val bidAmount = state.bidAmount.replace(",", "").toDoubleOrNull() ?: 0.0
        val bidType = state.bidType
        val dividendMode = state.dividendMode

        if (amount > 0 && months > 0) {
            val monthlyInstallment = amount / months
            val totalCommission = commissionAmount
            val commissionPerMonth = totalCommission / months
            
            val discount = if (bidType == CalculatorContract.BidType.TOTAL_BID) {
                (amount - bidAmount).coerceAtLeast(0.0) - totalCommission
            } else {
                bidAmount - totalCommission
            }.coerceAtLeast(0.0)
            
            val dividend = com.example.chitassist.domain.model.DividendCalculator.calculateDividend(
                totalMembers = months,
                installmentNumber = installmentNumber,
                discount = discount,
                mode = dividendMode
            )

            val actualPayment = monthlyInstallment - dividend

            _viewState.update { 
                it.copy(
                    monthlyInstallment = monthlyInstallment,
                    totalCommission = totalCommission,
                    dividend = dividend,
                    actualPayment = actualPayment,
                    effectiveMonthlyInstallment = monthlyInstallment + commissionPerMonth
                )
            }
        } else {
            _viewState.update { 
                it.copy(
                    monthlyInstallment = 0.0,
                    totalCommission = 0.0,
                    dividend = 0.0,
                    actualPayment = 0.0,
                    effectiveMonthlyInstallment = 0.0
                )
            }
        }
    }

    private fun formatInput(input: String): String {
        val clean = input.replace(",", "")
        if (clean.isEmpty()) return ""
        return try {
            val parts = clean.split(".")
            val integerPart = parts[0].toLong()
            val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("en", "IN"))
            val formattedInt = formatter.format(integerPart)
            if (parts.size > 1) {
                "$formattedInt.${parts[1]}"
            } else if (clean.endsWith(".")) {
                "$formattedInt."
            } else {
                formattedInt
            }
        } catch (e: Exception) {
            clean
        }
    }
}

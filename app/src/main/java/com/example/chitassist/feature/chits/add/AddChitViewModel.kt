package com.example.chitassist.feature.chits.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.repository.ChitRepository
import com.example.chitassist.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddChitViewModel @Inject constructor(
    private val chitRepository: ChitRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddChitContract.State())
    val state: StateFlow<AddChitContract.State> = _state.asStateFlow()

    private val _effect = Channel<AddChitContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private var editingChitId: Long? = null

    fun onIntent(intent: AddChitContract.Intent) {
        when (intent) {
            is AddChitContract.Intent.LoadChit -> loadChitForEditing(intent.chitId)
            is AddChitContract.Intent.OnNameChanged -> _state.update { it.copy(name = intent.name) }
            is AddChitContract.Intent.OnOrganizerChanged -> _state.update { it.copy(organizer = intent.organizer) }
            is AddChitContract.Intent.OnTotalValueChanged -> _state.update { it.copy(totalValue = formatInput(intent.value)) }
            is AddChitContract.Intent.OnMonthlyContributionChanged -> _state.update { it.copy(monthlyContribution = formatInput(intent.value)) }
            is AddChitContract.Intent.OnInstallmentsChanged -> _state.update { it.copy(numberOfInstallments = intent.value) }
            is AddChitContract.Intent.OnCommissionAmountChanged -> _state.update { it.copy(commissionAmount = formatInput(intent.value)) }
            is AddChitContract.Intent.OnDueDateChanged -> _state.update { it.copy(paymentDueDay = intent.day) }
            is AddChitContract.Intent.OnStartDateChanged -> _state.update { it.copy(startDate = intent.date) }
            is AddChitContract.Intent.OnIntervalChanged -> _state.update { it.copy(intervalMonths = intent.interval) }
            is AddChitContract.Intent.OnDividendModeChanged -> _state.update { it.copy(dividendMode = intent.mode) }
            AddChitContract.Intent.OnSaveClicked -> saveChit()
        }
    }

    private fun loadChitForEditing(chitId: Long) {
        editingChitId = chitId
        viewModelScope.launch {
            chitRepository.getChitById(chitId).firstOrNull()?.let { chit ->
                _state.update {
                    it.copy(
                        name = chit.name,
                        organizer = chit.organizer ?: "",
                        totalValue = formatInput(chit.totalValue.toString()),
                        monthlyContribution = formatInput(chit.monthlyContribution.toString()),
                        numberOfInstallments = chit.numberOfInstallments.toString(),
                        commissionAmount = formatInput(chit.commissionAmount.toString()),
                        startDate = chit.startDate,
                        paymentDueDay = chit.paymentDueDay.toString(),
                        intervalMonths = chit.intervalMonths,
                        dividendMode = chit.dividendMode
                    )
                }
            }
        }
    }

    private fun saveChit() {
        val currentState = _state.value
        if (currentState.name.isBlank() || currentState.totalValue.isBlank()) {
            viewModelScope.launch {
                _effect.send(AddChitContract.Effect.ShowError("Name and Total Value are required"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = userRepository.getLoggedInUser().first()
            val userId = user?.id

            if (userId == null || userId == -1L) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(AddChitContract.Effect.ShowError("User session expired. Please login again."))
                return@launch
            }
            
            val chit = Chit(
                id = editingChitId ?: 0L,
                userId = userId,
                name = currentState.name,
                organizer = currentState.organizer.takeIf { it.isNotBlank() },
                totalValue = currentState.totalValue.replace(",", "").toDoubleOrNull() ?: 0.0,
                monthlyContribution = currentState.monthlyContribution.replace(",", "").toDoubleOrNull() ?: 0.0,
                numberOfInstallments = currentState.numberOfInstallments.toIntOrNull() ?: 0,
                commissionAmount = currentState.commissionAmount.replace(",", "").toDoubleOrNull() ?: 0.0,
                startDate = currentState.startDate,
                paymentDueDay = currentState.paymentDueDay.toIntOrNull() ?: 1,
                intervalMonths = currentState.intervalMonths,
                dividendMode = currentState.dividendMode
            )

            if (editingChitId != null) {
                chitRepository.updateChit(chit)
                // When editing, if structural details change, we may need to regenerate or update payments.
                // For now, let's at least update the name in all related payments.
                val payments = chitRepository.getPaymentsForChit(chit.id).first()
                payments.forEach { 
                    if (it.chitName != chit.name) {
                        chitRepository.updatePayment(it.copy(chitName = chit.name))
                    }
                }
            } else {
                val chitId = chitRepository.insertChit(chit)
                
                // Generate payments based on intervalMonths
                val payments = mutableListOf<com.example.chitassist.domain.model.Payment>()
                val calendar = Calendar.getInstance().apply { timeInMillis = chit.startDate }
                
                for (i in 1..chit.numberOfInstallments) {
                    payments.add(
                        com.example.chitassist.domain.model.Payment(
                            chitId = chitId,
                            chitName = chit.name,
                            installmentNumber = i,
                            dueDate = calendar.timeInMillis,
                            amount = chit.monthlyContribution,
                            status = com.example.chitassist.domain.model.PaymentStatus.PENDING
                        )
                    )
                    calendar.add(Calendar.MONTH, chit.intervalMonths)
                }
                
                payments.forEach { chitRepository.insertPayment(it) }
            }

            _state.update { it.copy(isLoading = false) }
            _effect.send(AddChitContract.Effect.NavigationBack)
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

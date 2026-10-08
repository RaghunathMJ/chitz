package com.example.chitassist.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitassist.domain.repository.ChitRepository
import com.example.chitassist.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chitRepository: ChitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardContract.State())
    val state: StateFlow<DashboardContract.State> = _state.asStateFlow()

    private val _effect = Channel<DashboardContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadDashboard()
    }

    fun onIntent(intent: DashboardContract.Intent) {
        when (intent) {
            DashboardContract.Intent.LoadDashboard -> loadDashboard()
            DashboardContract.Intent.OnLogoutClicked -> logout()
            is DashboardContract.Intent.OnDeleteChitClicked -> deleteChit(intent.chit)
            is DashboardContract.Intent.OnEditChitClicked -> {
                viewModelScope.launch {
                    _effect.send(DashboardContract.Effect.NavigationToEditChit(intent.chit.id))
                }
            }
        }
    }

    private fun deleteChit(chit: com.example.chitassist.domain.model.Chit) {
        viewModelScope.launch {
            chitRepository.deleteChit(chit)
            // The flow should automatically update the UI since we are collecting getChitsByUserId
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            userRepository.getLoggedInUser().flatMapLatest { user ->
                if (user != null) {
                    combine(
                        chitRepository.getChitsByUserId(user.id),
                        chitRepository.getUpcomingPayments(user.id, 3)
                    ) { chits, upcoming ->
                        Triple(user, chits, upcoming)
                    }
                } else {
                    flowOf(null)
                }
            }.collect { data ->
                if (data != null) {
                    _state.update { 
                        it.copy(
                            user = data.first,
                            chits = data.second,
                            upcomingPayments = data.third,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.clearSession()
            _effect.send(DashboardContract.Effect.NavigationToLogin)
        }
    }
}

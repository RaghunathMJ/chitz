package com.example.chitassist.feature.chits.list

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
class ChitListViewModel @Inject constructor(
    private val chitRepository: ChitRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChitListContract.State())
    val state: StateFlow<ChitListContract.State> = _state.asStateFlow()

    private val _effect = Channel<ChitListContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadChits()
    }

    fun handleIntent(intent: ChitListContract.Intent) {
        when (intent) {
            is ChitListContract.Intent.LoadChits -> loadChits()
            is ChitListContract.Intent.DeleteChit -> deleteChit(intent.chit)
            is ChitListContract.Intent.EditChit -> {
                viewModelScope.launch {
                    _effect.send(ChitListContract.Effect.NavigationToEditChit(intent.chit.id))
                }
            }
        }
    }

    private fun deleteChit(chit: com.example.chitassist.domain.model.Chit) {
        viewModelScope.launch {
            chitRepository.deleteChit(chit)
        }
    }

    private fun loadChits() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.getLoggedInUser().flatMapLatest { user ->
                if (user != null) {
                    chitRepository.getChitsByUserId(user.id)
                } else {
                    flowOf(emptyList())
                }
            }.collect { chits ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        chits = chits
                    )
                }
            }
        }
    }
}

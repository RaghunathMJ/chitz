package com.example.chitassist.feature.profile.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitassist.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileContract.State())
    val state: StateFlow<ProfileContract.State> = _state.asStateFlow()

    private val _effect = Channel<ProfileContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(ProfileContract.Intent.LoadProfile)
    }

    fun handleIntent(intent: ProfileContract.Intent) {
        when (intent) {
            is ProfileContract.Intent.LoadProfile -> loadProfile()
            is ProfileContract.Intent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.getLoggedInUser()
                .collect { user ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            user = user
                        )
                    }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.clearSession()
            _effect.send(ProfileContract.Effect.NavigateToLogin)
        }
    }
}

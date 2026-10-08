package com.example.chitassist.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitassist.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginContract.State())
    val state: StateFlow<LoginContract.State> = _state.asStateFlow()

    private val _effect = Channel<LoginContract.Effect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.OnMobileNumberChanged -> {
                _state.update { it.copy(mobileNumber = intent.mobileNumber) }
            }
            LoginContract.Intent.OnLoginClicked -> {
                login()
            }
        }
    }

    private fun login() {
        val mobileNumber = _state.value.mobileNumber
        if (mobileNumber.isBlank()) {
            viewModelScope.launch {
                _effect.send(LoginContract.Effect.ShowError("Mobile number is required"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val user = userRepository.getUserByMobile(mobileNumber)
            if (user != null) {
                userRepository.saveSession(user.id)
                _state.update { it.copy(isLoading = false) }
                _effect.send(LoginContract.Effect.NavigationToDashboard)
            } else {
                _state.update { it.copy(isLoading = false, error = "User not found. Please register.") }
                _effect.send(LoginContract.Effect.NavigationToRegister)
            }
        }
    }
}

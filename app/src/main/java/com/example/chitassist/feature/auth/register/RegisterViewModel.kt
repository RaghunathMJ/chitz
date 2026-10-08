package com.example.chitassist.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitassist.domain.model.User
import com.example.chitassist.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterContract.State())
    val state: StateFlow<RegisterContract.State> = _state.asStateFlow()

    private val _effect = Channel<RegisterContract.Effect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterContract.Intent) {
        when (intent) {
            is RegisterContract.Intent.OnNameChanged -> {
                _state.update { it.copy(name = intent.name) }
            }
            is RegisterContract.Intent.OnMobileNumberChanged -> {
                _state.update { it.copy(mobileNumber = intent.mobileNumber) }
            }
            is RegisterContract.Intent.OnEmailChanged -> {
                _state.update { it.copy(email = intent.email) }
            }
            is RegisterContract.Intent.OnCityChanged -> {
                _state.update { it.copy(city = intent.city) }
            }
            is RegisterContract.Intent.OnDateOfBirthChanged -> {
                _state.update { it.copy(dateOfBirth = intent.dateOfBirth) }
            }
            RegisterContract.Intent.OnRegisterClicked -> {
                register()
            }
        }
    }

    private fun register() {
        val currentState = _state.value
        if (currentState.name.isBlank() || currentState.mobileNumber.isBlank()) {
            viewModelScope.launch {
                _effect.send(RegisterContract.Effect.ShowError("Name and Mobile Number are required"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val user = User(
                id = 0,
                name = currentState.name,
                mobileNumber = currentState.mobileNumber,
                email = currentState.email.takeIf { it.isNotBlank() },
                city = currentState.city.takeIf { it.isNotBlank() },
                dateOfBirth = currentState.dateOfBirth.takeIf { it.isNotBlank() }
            )

            userRepository.registerUser(user).onSuccess { id ->
                userRepository.saveSession(id)
                _state.update { it.copy(isLoading = false) }
                _effect.send(RegisterContract.Effect.NavigationToDashboard)
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
                _effect.send(RegisterContract.Effect.ShowError(e.message ?: "Registration failed"))
            }
        }
    }
}

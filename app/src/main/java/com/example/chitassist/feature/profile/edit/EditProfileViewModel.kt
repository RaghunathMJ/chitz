package com.example.chitassist.feature.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitassist.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileContract.State())
    val state: StateFlow<EditProfileContract.State> = _state.asStateFlow()

    private val _effect = Channel<EditProfileContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(EditProfileContract.Intent.LoadProfile)
    }

    fun handleIntent(intent: EditProfileContract.Intent) {
        when (intent) {
            is EditProfileContract.Intent.LoadProfile -> loadProfile()
            is EditProfileContract.Intent.OnNameChanged -> _state.update { it.copy(name = intent.name) }
            is EditProfileContract.Intent.OnEmailChanged -> _state.update { it.copy(email = intent.email) }
            is EditProfileContract.Intent.OnCityChanged -> _state.update { it.copy(city = intent.city) }
            is EditProfileContract.Intent.SaveProfile -> saveProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.getLoggedInUser()
                .firstOrNull()?.let { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            name = user.name,
                            email = user.email ?: "",
                            city = user.city ?: ""
                        )
                    }
                }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val currentState = _state.value
            val currentUser = currentState.user ?: return@launch
            
            if (currentState.name.isBlank()) {
                _effect.send(EditProfileContract.Effect.ShowError("Name cannot be empty"))
                return@launch
            }

            val updatedUser = currentUser.copy(
                name = currentState.name,
                email = currentState.email.ifBlank { null },
                city = currentState.city.ifBlank { null }
            )

            userRepository.updateUser(updatedUser)
                .onSuccess {
                    _effect.send(EditProfileContract.Effect.NavigateBack)
                }
                .onFailure { error ->
                    _effect.send(EditProfileContract.Effect.ShowError(error.message ?: "Failed to update profile"))
                }
        }
    }
}

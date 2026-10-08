package com.example.chitassist.feature.auth.register

import com.example.chitassist.domain.model.User

class RegisterContract {
    data class State(
        val name: String = "",
        val mobileNumber: String = "",
        val email: String = "",
        val city: String = "",
        val dateOfBirth: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Intent {
        data class OnNameChanged(val name: String) : Intent()
        data class OnMobileNumberChanged(val mobileNumber: String) : Intent()
        data class OnEmailChanged(val email: String) : Intent()
        data class OnCityChanged(val city: String) : Intent()
        data class OnDateOfBirthChanged(val dateOfBirth: String) : Intent()
        object OnRegisterClicked : Intent()
    }

    sealed class Effect {
        object NavigationToDashboard : Effect()
        data class ShowError(val message: String) : Effect()
    }
}

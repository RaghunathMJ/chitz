package com.example.chitassist.feature.auth.login

class LoginContract {
    data class State(
        val mobileNumber: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Intent {
        data class OnMobileNumberChanged(val mobileNumber: String) : Intent()
        object OnLoginClicked : Intent()
    }

    sealed class Effect {
        object NavigationToDashboard : Effect()
        object NavigationToRegister : Effect()
        data class ShowError(val message: String) : Effect()
    }
}

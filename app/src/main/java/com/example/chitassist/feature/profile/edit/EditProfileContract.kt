package com.example.chitassist.feature.profile.edit

import com.example.chitassist.domain.model.User

class EditProfileContract {
    data class State(
        val isLoading: Boolean = false,
        val user: User? = null,
        val name: String = "",
        val email: String = "",
        val city: String = "",
        val error: String? = null,
        val isSuccess: Boolean = false
    )

    sealed class Intent {
        object LoadProfile : Intent()
        data class OnNameChanged(val name: String) : Intent()
        data class OnEmailChanged(val email: String) : Intent()
        data class OnCityChanged(val city: String) : Intent()
        object SaveProfile : Intent()
    }

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}

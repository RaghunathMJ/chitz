package com.example.chitassist.feature.profile.profile

import com.example.chitassist.domain.model.User

class ProfileContract {
    data class State(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null
    )

    sealed class Intent {
        object LoadProfile : Intent()
        object Logout : Intent()
    }

    sealed class Effect {
        object NavigateToLogin : Effect()
        object NavigateToEditProfile : Effect()
    }
}

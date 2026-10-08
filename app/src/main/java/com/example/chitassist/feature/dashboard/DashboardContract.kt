package com.example.chitassist.feature.dashboard

import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.model.Payment
import com.example.chitassist.domain.model.User

class DashboardContract {
    data class State(
        val user: User? = null,
        val chits: List<Chit> = emptyList(),
        val upcomingPayments: List<Payment> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Intent {
        object LoadDashboard : Intent()
        object OnLogoutClicked : Intent()
        data class OnDeleteChitClicked(val chit: Chit) : Intent()
        data class OnEditChitClicked(val chit: Chit) : Intent()
    }

    sealed class Effect {
        object NavigationToLogin : Effect()
        data class NavigationToEditChit(val chitId: Long) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}

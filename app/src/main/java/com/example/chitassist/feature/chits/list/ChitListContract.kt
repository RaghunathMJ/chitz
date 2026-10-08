package com.example.chitassist.feature.chits.list

import com.example.chitassist.domain.model.Chit

class ChitListContract {
    data class State(
        val isLoading: Boolean = false,
        val chits: List<Chit> = emptyList(),
        val error: String? = null
    )

    sealed class Intent {
        object LoadChits : Intent()
        data class EditChit(val chit: Chit) : Intent()
        data class DeleteChit(val chit: Chit) : Intent()
    }

    sealed class Effect {
        data class ShowError(val message: String) : Effect()
        data class NavigationToEditChit(val chitId: Long) : Effect()
    }
}

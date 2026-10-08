package com.example.chitassist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitassist.core.navigation.Screen
import com.example.chitassist.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isLoggedIn = userRepository.isLoggedIn().first()
            _startDestination.value = if (isLoggedIn) Screen.Main.route else Screen.Login.route
        }
    }
}

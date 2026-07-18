package com.fameli.budget.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(val isLoading: Boolean = false, val error: String? = null, val isLoggedIn: Boolean = false)

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: FirebaseAuthRepository) : ViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val isLoginMode = MutableStateFlow(true)
    val uiState = MutableStateFlow(AuthUiState())
    val isLoggedIn = authRepository.currentUser.map { it != null }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun updateEmail(v: String) { email.value = v }
    fun updatePassword(v: String) { password.value = v }
    fun toggleMode() { isLoginMode.value = !isLoginMode.value }

    fun authenticate() = viewModelScope.launch {
        uiState.value = AuthUiState(isLoading = true)
        val result = if (isLoginMode.value) authRepository.signIn(email.value, password.value)
        else authRepository.signUp(email.value, password.value)
        result.fold({ uiState.value = AuthUiState(isLoggedIn = true) }, { uiState.value = AuthUiState(error = it.message) })
    }

    fun signInAnonymously() = viewModelScope.launch {
        uiState.value = AuthUiState(isLoading = true)
        authRepository.signInAnonymously().fold({ uiState.value = AuthUiState(isLoggedIn = true) }, { uiState.value = AuthUiState(error = it.message) })
    }
}

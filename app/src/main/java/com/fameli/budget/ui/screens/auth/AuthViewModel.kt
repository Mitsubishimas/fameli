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
    val showEmailForm = MutableStateFlow(false)
    val isLoggedIn = authRepository.currentUser.map { it != null }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun updateEmail(v: String) { email.value = v.trim() }
    fun updatePassword(v: String) { password.value = v.trim() }

    fun showEmailLogin() {
        isLoginMode.value = true
        showEmailForm.value = true
        email.value = ""
        password.value = ""
        uiState.value = AuthUiState()
    }

    fun showEmailRegister() {
        isLoginMode.value = false
        showEmailForm.value = true
        email.value = ""
        password.value = ""
        uiState.value = AuthUiState()
    }

    fun showMainScreen() {
        showEmailForm.value = false
        uiState.value = AuthUiState()
    }

    fun authenticate() = viewModelScope.launch {
        if (email.value.isBlank() || password.value.isBlank()) {
            uiState.value = AuthUiState(error = "Заполните все поля")
            return@launch
        }
        if (password.value.length < 6) {
            uiState.value = AuthUiState(error = "Пароль должен быть не менее 6 символов")
            return@launch
        }

        uiState.value = AuthUiState(isLoading = true)
        val result = if (isLoginMode.value) {
            authRepository.signIn(email.value, password.value)
        } else {
            authRepository.signUp(email.value, password.value)
        }
        result.fold(
            onSuccess = { uiState.value = AuthUiState(isLoggedIn = true) },
            onFailure = {
                val msg = when {
                    it.message?.contains("email already") == true -> "Этот email уже зарегистрирован"
                    it.message?.contains("invalid email") == true -> "Неверный email"
                    it.message?.contains("password is invalid") == true -> "Неверный пароль"
                    it.message?.contains("user not found") == true -> "Пользователь не найден. Создайте аккаунт"
                    else -> it.message ?: "Ошибка"
                }
                uiState.value = AuthUiState(error = msg)
            }
        )
    }

    fun signInAnonymously() = viewModelScope.launch {
        uiState.value = AuthUiState(isLoading = true)
        authRepository.signInAnonymously().fold(
            onSuccess = { uiState.value = AuthUiState(isLoggedIn = true) },
            onFailure = { uiState.value = AuthUiState(error = "Ошибка входа. Проверьте интернет.") }
        )
    }
}

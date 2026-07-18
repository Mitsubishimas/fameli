package com.fameli.budget.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreen(onSuccess: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLogin by viewModel.isLoginMode.collectAsState()
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn) { if (state.isLoggedIn) onSuccess() }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(Modifier.fillMaxWidth().padding(24.dp)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("💰 Fameli", style = MaterialTheme.typography.headlineLarge)
                OutlinedTextField(email, { viewModel.updateEmail(it) }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), enabled = !state.isLoading)
                OutlinedTextField(password, { viewModel.updatePassword(it) }, label = { Text("Пароль") }, modifier = Modifier.fillMaxWidth(), visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), enabled = !state.isLoading)
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button({ viewModel.authenticate() }, Modifier.fillMaxWidth(), enabled = !state.isLoading) { Text(if (isLogin) "Войти" else "Зарегистрироваться") }
                TextButton({ viewModel.toggleMode() }) { Text(if (isLogin) "Создать аккаунт" else "Уже есть аккаунт?") }
                TextButton({ viewModel.signInAnonymously() }) { Text("Пропустить") }
            }
        }
    }
}

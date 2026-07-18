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
    val showEmailForm by viewModel.showEmailForm.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn) { if (state.isLoggedIn) onSuccess() }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(Modifier.fillMaxWidth().padding(24.dp)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("💰", style = MaterialTheme.typography.displayMedium)
                Text("Fameli", style = MaterialTheme.typography.headlineLarge)
                Text("Семейный бюджет", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (!showEmailForm) {
                    // Кнопки входа
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.showEmailLogin() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Email, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Войти по Email")
                    }

                    OutlinedButton(
                        onClick = { viewModel.showEmailRegister() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Создать аккаунт")
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TextButton(onClick = { viewModel.signInAnonymously() }) {
                        Text("Пропустить и войти без аккаунта")
                    }
                } else {
                    // Форма Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Filled.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !state.isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = { Text("Пароль") },
                        leadingIcon = { Icon(Icons.Filled.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = !state.isLoading,
                        singleLine = true
                    )

                    state.error?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Button(
                        onClick = { viewModel.authenticate() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading && email.isNotBlank() && password.isNotBlank()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (isLogin) "Войти" else "Зарегистрироваться")
                    }

                    TextButton(onClick = { viewModel.showMainScreen() }) {
                        Text("← Назад")
                    }
                }
            }
        }
    }
}

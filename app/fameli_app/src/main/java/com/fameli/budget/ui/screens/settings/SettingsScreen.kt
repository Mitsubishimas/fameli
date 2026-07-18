package com.fameli.budget.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val token by viewModel.yandexToken.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Яндекс.Диск", style = MaterialTheme.typography.titleMedium)
        if (token == null) {
            Button({ viewModel.login() }, Modifier.fillMaxWidth()) { Text("Войти через Яндекс") }
        } else {
            Text("Подключено ✅")
            TextButton({ viewModel.logout() }) { Text("Отключить") }
        }
        Button({ viewModel.syncNow() }, Modifier.fillMaxWidth()) { Text("Синхронизировать") }
    }
}

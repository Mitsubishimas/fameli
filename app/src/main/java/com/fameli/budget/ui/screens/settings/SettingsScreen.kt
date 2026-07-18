package com.fameli.budget.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.firebase.FirebaseAuthRepository
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val token by viewModel.yandexToken.collectAsState()
    val lastSync by viewModel.lastSync.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val context = LocalContext.current

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        // Аккаунт
        item {
            Text("Аккаунт", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Button(onClick = { viewModel.logout() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Icon(Icons.Filled.Logout, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Выйти из аккаунта")
                    }
                }
            }
        }

        // Яндекс.Диск
        item {
            Text("Яндекс.Диск", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (token == null) {
                        Button(onClick = { viewModel.login() }, modifier = Modifier.fillMaxWidth()) { Text("Войти через Яндекс ID") }
                    } else {
                        Text("Подключено ✅")
                        TextButton(onClick = { viewModel.logout() }) { Text("Отключить") }
                    }
                }
            }
        }

        // Синхронизация
        item {
            Text("Синхронизация", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.syncNow() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Синхронизация...")
                        } else {
                            Icon(Icons.Filled.Sync, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Синхронизировать сейчас")
                        }
                    }
                    if (lastSync > 0) {
                        Text("Последняя: ${java.text.SimpleDateFormat("dd.MM HH:mm").format(java.util.Date(lastSync))}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Обновления
        item {
            Text("Обновления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Mitsubishimas/fameli/releases"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Update, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Проверить обновления")
                    }
                }
            }
        }

        // Экспорт
        item {
            Text("Экспорт", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Button(onClick = { viewModel.exportCsv() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Download, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Экспорт в CSV")
                    }
                }
            }
        }

        // О приложении
        item {
            Text("О приложении", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Fameli v0.1.0", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("Семейный бюджет с облачной синхронизацией", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Made with ❤️ by Fameli Team", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

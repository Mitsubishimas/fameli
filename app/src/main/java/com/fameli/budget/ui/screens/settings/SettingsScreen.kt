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

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToFamily: (() -> Unit)? = null
) {
    val updateStatus by viewModel.updateStatus.collectAsState()
    val context = LocalContext.current

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        // Семья
        item {
            Text("👨‍👩‍👧‍👦 Семья", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onNavigateToFamily?.invoke() },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Icon(Icons.Filled.People, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Управление семьёй и синхронизация")
                }
            }
        }

        // Аккаунт
        item { Text("Аккаунт", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Выйти из аккаунта")
                }
            }
        }

        // Обновления
        item { Text("Обновления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Текущая версия: ${viewModel.currentVersion}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    
                    when (val status = updateStatus) {
                        is UpdateStatus.Checking -> Row {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Проверка...")
                        }
                        is UpdateStatus.UpdateAvailable -> {
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Доступна версия ${status.version}", fontWeight = FontWeight.Bold)
                                    Button(onClick = {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(status.url)))
                                    }, modifier = Modifier.fillMaxWidth()) { Text("Скачать") }
                                }
                            }
                        }
                        is UpdateStatus.UpToDate -> Text("✅ У вас последняя версия", color = MaterialTheme.colorScheme.primary)
                        is UpdateStatus.Error -> Text("❌ ${status.message}", color = MaterialTheme.colorScheme.error)
                        else -> {}
                    }

                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.checkForUpdates() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = updateStatus !is UpdateStatus.Checking
                    ) { Text("Проверить обновления") }
                }
            }
        }

        // О приложении
        item { Text("О приложении", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Семейный бюджет и планирование", fontWeight = FontWeight.Bold)
                    Text("Версия ${viewModel.currentVersion}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

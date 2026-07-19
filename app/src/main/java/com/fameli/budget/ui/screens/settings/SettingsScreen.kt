package com.fameli.budget.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val updateStatus by viewModel.updateStatus.collectAsState()
    val context = LocalContext.current

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
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

        item {
            Text("Обновления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Версия: ${viewModel.currentVersion}")
                    Spacer(Modifier.height(8.dp))
                    
                    when (val s = updateStatus) {
                        is UpdateStatus.Checking -> Row {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Проверка...")
                        }
                        is UpdateStatus.UpdateAvailable -> {
                            Text("Доступна версия ${s.version}", fontWeight = FontWeight.Bold)
                            Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(s.url))) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Скачать обновление")
                            }
                        }
                        is UpdateStatus.UpToDate -> Text("У вас последняя версия", color = MaterialTheme.colorScheme.primary)
                        is UpdateStatus.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                        else -> {}
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { viewModel.checkForUpdates() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Проверить обновления")
                    }
                }
            }
        }

        item {
            Text("О приложении", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Семейный бюджет", fontWeight = FontWeight.Bold)
                    Text("Версия ${viewModel.currentVersion}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

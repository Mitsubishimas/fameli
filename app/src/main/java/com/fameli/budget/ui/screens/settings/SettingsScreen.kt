package com.fameli.budget.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import com.fameli.budget.ui.screens.family.FamilyViewModel

@Composable
fun SettingsScreen(
    settingsVM: SettingsViewModel = hiltViewModel(),
    onNavigateToCategories: (() -> Unit)? = null,
    onNavigateToFamily: (() -> Unit)? = null
) {
    val userName by settingsVM.userName.collectAsState()
    val userEmail by settingsVM.userEmail.collectAsState()
    var editName by remember { mutableStateOf("") }
    var showEditName by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        // ПРОФИЛЬ
        item { Text("Профиль", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.Filled.Person, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(userName, fontWeight = FontWeight.Bold)
                            Text(userEmail, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { editName = userName; showEditName = true }) { Icon(Icons.Filled.Edit, "Изменить имя") }
                    }
                }
            }
        }

        // Семья
        item { Text("Семья", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Button(onClick = { onNavigateToFamily?.invoke() }, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Icon(Icons.Filled.People, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Управление семьёй и синхронизация")
                }
            }
        }

        // Категории
        item { Text("Данные", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Button(onClick = { onNavigateToCategories?.invoke() }, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Icon(Icons.Filled.Category, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Категории")
                }
            }
        }

        // Аккаунт
        item { Text("Аккаунт", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Button(onClick = { settingsVM.logout() }, modifier = Modifier.padding(16.dp).fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Выйти") }
            }
        }

        // Обновления
        item { Text("Обновления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Версия: ${settingsVM.currentVersion}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { settingsVM.checkForUpdates() }, modifier = Modifier.fillMaxWidth()) { Text("Проверить обновления") }
                }
            }
        }
    }

    if (showEditName) {
        AlertDialog(
            onDismissRequest = { showEditName = false },
            title = { Text("Изменить имя") },
            text = { OutlinedTextField(editName, { editName = it }, label = { Text("Ваше имя") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { Button(onClick = { settingsVM.updateUserName(editName); showEditName = false }, enabled = editName.isNotBlank()) { Text("Сохранить") } },
            dismissButton = { TextButton(onClick = { showEditName = false }) { Text("Отмена") } }
        )
    }
}

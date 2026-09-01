package com.fameli.budget.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.remote.AppLogger
import com.fameli.budget.ui.screens.family.FamilyViewModel

@Composable
fun SettingsScreen(
    settingsVM: SettingsViewModel = hiltViewModel(),
    familyVM: FamilyViewModel = hiltViewModel()
) {
    val logs by AppLogger.logs.collectAsState()
    val familyId by familyVM.familyId.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        item { Text("Семья", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("ID: ${familyId ?: "не выбрана"}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { familyVM.forceSync() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Синхронизировать")
                    }
                }
            }
        }

        item { Text("Обновления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Версия: ${settingsVM.currentVersion}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { settingsVM.checkForUpdates() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Проверить обновления")
                    }
                }
            }
        }

        item { Text("Журнал", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    SelectionContainer {
                        Text(
                            text = if (logs.isEmpty()) "Журнал пуст" else logs.joinToString("\n"),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { AppLogger.clear() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Очистить журнал")
                    }
                }
            }
        }

        item { Text("Аккаунт", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Button(onClick = { settingsVM.logout() }, modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Выйти")
                }
            }
        }
    }
}

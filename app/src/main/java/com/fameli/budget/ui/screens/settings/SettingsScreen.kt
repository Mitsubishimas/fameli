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
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.fameli.budget.ui.screens.family.FamilyViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsVM: SettingsViewModel = hiltViewModel(),
    familyVM: FamilyViewModel = hiltViewModel(),
    onNavigateToCategories: (() -> Unit)? = null
) {
    val familyId by familyVM.familyId.collectAsState()
    val message by familyVM.message.collectAsState()
    val isSyncing by familyVM.isSyncing.collectAsState()
    var joinCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authRepo: FirebaseAuthRepository = hiltViewModel()
    var userName by remember { mutableStateOf(authRepo.getUserName()) }
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
                            Text(authRepo.getUserEmail(), style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { showEditName = true }) { Icon(Icons.Filled.Edit, "Изменить имя") }
                    }
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

        // Семья
        item { Text("Семья", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (familyId == null) {
                        Button(onClick = { familyVM.createFamily() }, modifier = Modifier.fillMaxWidth()) { Text("Создать семейную группу") }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(joinCode, { joinCode = it }, label = { Text("Код семьи") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = { familyVM.joinFamily(joinCode); joinCode = "" }, modifier = Modifier.fillMaxWidth(), enabled = joinCode.isNotBlank()) { Text("Присоединиться") }
                    } else {
                        Text("Семья активна", fontWeight = FontWeight.Bold)
                        Text("Код: ${familyId!!.take(12)}...")
                        IconButton(onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("code", familyId))
                            Toast.makeText(context, "Код скопирован", Toast.LENGTH_SHORT).show()
                        }) { Icon(Icons.Filled.ContentCopy, "Копировать") }
                        Button(onClick = { familyVM.forceSync() }, modifier = Modifier.fillMaxWidth(), enabled = !isSyncing) {
                            if (isSyncing) { CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)) }
                            Text("Синхронизировать")
                        }
                        message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        OutlinedButton(onClick = { familyVM.leaveFamily() }, modifier = Modifier.fillMaxWidth()) { Text("Покинуть семью") }
                    }
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

    // Диалог изменения имени
    if (showEditName) {
        val scope = rememberCoroutineScope()
        AlertDialog(
            onDismissRequest = { showEditName = false },
            title = { Text("Изменить имя") },
            text = {
                OutlinedTextField(userName, { userName = it }, label = { Text("Ваше имя") }, modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        authRepo.updateUserName(userName)
                        showEditName = false
                    }
                }, enabled = userName.isNotBlank()) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { showEditName = false }) { Text("Отмена") } }
        )
    }
}

package com.fameli.budget.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
    familyVM: FamilyViewModel = hiltViewModel()
) {
    val updateStatus by settingsVM.updateStatus.collectAsState()
    val familyId by familyVM.familyId.collectAsState()
    val isLoading by familyVM.isLoading.collectAsState()
    var joinCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        // СЕМЬЯ
        item { Text("👨‍👩‍👧‍👦 Семья", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (familyId == null) {
                        Button(onClick = { familyVM.createFamily() }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                            Text("Создать семейную группу")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(joinCode, { joinCode = it }, label = { Text("Код семьи") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = { familyVM.joinFamily(joinCode); joinCode = "" }, modifier = Modifier.fillMaxWidth(), enabled = joinCode.isNotBlank()) {
                            Text("Присоединиться")
                        }
                    } else {
                        Text("✅ Семья активна", fontWeight = FontWeight.Bold)
                        Text("Код: ${familyId!!.take(12)}...")
                        IconButton(onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("code", familyId))
                            Toast.makeText(context, "Код скопирован", Toast.LENGTH_SHORT).show()
                        }) { Icon(Icons.Filled.ContentCopy, "Копировать") }
                        OutlinedButton(onClick = { familyVM.leaveFamily() }, modifier = Modifier.fillMaxWidth()) { Text("Покинуть семью") }
                    }
                }
            }
        }

        // АККАУНТ
        item { Text("Аккаунт", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Button(onClick = { settingsVM.logout() }, modifier = Modifier.padding(16.dp).fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Выйти")
                }
            }
        }

        // ОБНОВЛЕНИЯ
        item { Text("Обновления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Версия: ${settingsVM.currentVersion}")
                    when (val s = updateStatus) {
                        is UpdateStatus.Checking -> Text("Проверка...")
                        is UpdateStatus.UpdateAvailable -> {
                            Text("Доступна ${s.version}", fontWeight = FontWeight.Bold)
                            Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(s.url))) }, modifier = Modifier.fillMaxWidth()) { Text("Скачать") }
                        }
                        is UpdateStatus.UpToDate -> Text("✅ Актуально")
                        is UpdateStatus.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                        else -> {}
                    }
                    OutlinedButton(onClick = { settingsVM.checkForUpdates() }, modifier = Modifier.fillMaxWidth()) { Text("Проверить обновления") }
                }
            }
        }
    }
}

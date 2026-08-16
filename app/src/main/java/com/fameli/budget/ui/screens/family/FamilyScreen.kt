package com.fameli.budget.ui.screens.family

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

@Composable
fun FamilyScreen(viewModel: FamilyViewModel = hiltViewModel()) {
    val familyId by viewModel.familyId.collectAsState()
    val message by viewModel.message.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    var joinCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Загружаем семью при открытии
    LaunchedEffect(Unit) {
        viewModel.loadFamily()
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Семья", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }

        message?.let { msg ->
            item {
                Card(colors = CardDefaults.cardColors(
                    containerColor = if (msg.startsWith("Ошибка")) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )) { Text(msg, modifier = Modifier.padding(12.dp)) }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (familyId == null) {
                        Text("Нет семьи")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.createFamily() }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                            Text("Создать семейную группу")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(joinCode, { joinCode = it }, label = { Text("Код семьи") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = { viewModel.joinFamily(joinCode); joinCode = "" }, modifier = Modifier.fillMaxWidth(), enabled = joinCode.isNotBlank()) {
                            Text("Присоединиться")
                        }
                    } else {
                        Text("Семья активна", fontWeight = FontWeight.Bold)
                        Text("Код: ${familyId!!.take(12)}...")
                        IconButton(onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("code", familyId))
                            Toast.makeText(context, "Код скопирован", Toast.LENGTH_SHORT).show()
                        }) { Icon(Icons.Filled.ContentCopy, "Копировать") }
                        Button(onClick = { viewModel.forceSync() }, modifier = Modifier.fillMaxWidth(), enabled = !isSyncing) {
                            if (isSyncing) { CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)) }
                            Text("Синхронизировать")
                        }
                        OutlinedButton(onClick = { viewModel.leaveFamily() }, modifier = Modifier.fillMaxWidth()) { Text("Покинуть семью") }
                    }
                }
            }
        }
    }
}

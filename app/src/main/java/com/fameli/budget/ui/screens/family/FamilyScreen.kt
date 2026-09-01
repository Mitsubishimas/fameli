package com.fameli.budget.ui.screens.family

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    val isSyncing by viewModel.isSyncing.collectAsState()
    val context = LocalContext.current

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (familyId == null) {
                        Text("Семья не найдена")
                        Text("Создайте семью через веб-интерфейс mastermitsu.ru")
                    } else {
                        Text("Семья активна", fontWeight = FontWeight.Bold)
                        Text("Код: ${familyId!!.take(12)}...")
                        IconButton(onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("code", familyId))
                            Toast.makeText(context, "Скопировано", Toast.LENGTH_SHORT).show()
                        }) { Text("📋 Копировать") }
                        
                        Button(
                            onClick = { viewModel.forceSync() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSyncing
                        ) {
                            if (isSyncing) Text("Синхронизация...")
                            else Text("🔄 Синхронизировать")
                        }
                        message?.let { Text(it) }
                    }
                }
            }
        }
    }
}

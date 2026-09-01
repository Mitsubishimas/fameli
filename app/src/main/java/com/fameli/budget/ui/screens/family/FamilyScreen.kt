package com.fameli.budget.ui.screens.family

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FamilyScreen(viewModel: FamilyViewModel = hiltViewModel()) {
    val familyId by viewModel.familyId.collectAsState()
    val message by viewModel.message.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    var joinCode by remember { mutableStateOf("") }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Семья", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        message?.let {
            item { Card(Modifier.fillMaxWidth()) { Text(it, modifier = Modifier.padding(12.dp)) } }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (familyId == null) {
                        Text("Вы не в семье")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(joinCode, { joinCode = it }, label = { Text("Введите ID семьи") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.joinFamily(joinCode.trim()) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = joinCode.isNotBlank()
                        ) { Text("Присоединиться") }
                    } else {
                        Text("Семья активна", fontWeight = FontWeight.Bold)
                        Text("ID: $familyId")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.forceSync() }, modifier = Modifier.fillMaxWidth(), enabled = !isSyncing) {
                            Text(if (isSyncing) "Синхронизация..." else "Синхронизировать")
                        }
                    }
                }
            }
        }
    }
}

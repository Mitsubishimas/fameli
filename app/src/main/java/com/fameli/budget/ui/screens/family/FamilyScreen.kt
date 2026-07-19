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
    val isLoading by viewModel.isLoading.collectAsState()
    var joinCode by remember { mutableStateOf("") }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Семья", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        // Сообщение
        message?.let { msg ->
            item {
                Card(colors = CardDefaults.cardColors(
                    containerColor = if (msg.startsWith("Ошибка")) MaterialTheme.colorScheme.errorContainer 
                                     else MaterialTheme.colorScheme.primaryContainer
                )) {
                    Text(msg, modifier = Modifier.padding(12.dp))
                }
            }
        }

        // Состояние семьи
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (familyId == null) {
                        Text("Нет семейной группы")
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.createFamily() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        ) {
                            if (isLoading) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            else Text("Создать семейную группу")
                        }
                    } else {
                        Text("Семья активна", fontWeight = FontWeight.Bold)
                        Text("ID: ${familyId!!.take(12)}...", style = MaterialTheme.typography.bodySmall)
                        Text("Синхронизация включена", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { viewModel.leaveFamily() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Покинуть семью")
                        }
                    }
                }
            }
        }

        // Присоединение
        if (familyId == null) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Присоединиться", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(joinCode, { joinCode = it }, label = { Text("Код семьи") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.joinFamily(joinCode.trim()); joinCode = "" },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = joinCode.isNotBlank() && !isLoading
                        ) { Text("Присоединиться") }
                    }
                }
            }
        }
    }
}

package com.fameli.budget.ui.screens.family

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FamilyScreen(viewModel: FamilyViewModel = hiltViewModel()) {
    val familyId by viewModel.familyId.collectAsState()
    val familyName = "Моя семья"

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("👨‍👩‍👧‍👦 Семья", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (familyId == null) {
                        Text("У вас нет семейной группы", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.createFamily() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Создать семейную группу")
                        }
                    } else {
                        Text("Семья: $familyName", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text("ID семьи: ${familyId!!.take(8)}...", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Text("✅ Синхронизация активна", color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { viewModel.leaveFamily() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Покинуть семью")
                        }
                    }
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Присоединиться к семье", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    var joinCode by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = { joinCode = it },
                        label = { Text("Код семьи") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.joinFamily(joinCode) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = joinCode.isNotBlank()
                    ) { Text("Присоединиться") }
                }
            }
        }
    }
}

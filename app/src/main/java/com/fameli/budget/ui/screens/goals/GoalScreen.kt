package com.fameli.budget.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fameli.budget.data.local.entity.GoalEntity
import com.fameli.budget.data.local.entity.GoalTransactionEntity
import java.text.NumberFormat
import java.util.*

@Composable
fun GoalScreen(viewModel: GoalViewModel, showAddDialog: Boolean, onDismiss: () -> Unit) {
    val goals by viewModel.goals.collectAsState()
    var goalTitle by remember { mutableStateOf("") }
    var goalDesc by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("") }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("🎯 Цели", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        if (goals.isEmpty()) {
            item { Card(Modifier.fillMaxWidth()) { Text("Нет целей. Нажмите +", modifier = Modifier.padding(24.dp)) } }
        }
        items(goals) { goal ->
            GoalCard(goal, viewModel)
        }
        item { Spacer(Modifier.height(72.dp)) }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss(); goalTitle = ""; goalDesc = ""; goalTarget = "" },
            title = { Text("Новая цель") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(goalTitle, { goalTitle = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(goalDesc, { goalDesc = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(goalTarget, { goalTarget = it }, label = { Text("Сумма") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addGoal(goalTitle, goalDesc, goalTarget)
                    goalTitle = ""; goalDesc = ""; goalTarget = ""
                    onDismiss()
                }, enabled = goalTitle.isNotBlank()) { Text("Создать") }
            },
            dismissButton = { TextButton(onClick = { onDismiss() }) { Text("Отмена") } }
        )
    }
}

@Composable
fun GoalCard(goal: GoalEntity, viewModel: GoalViewModel) {
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    var showMoney by remember { mutableStateOf(false) }
    var moneyAmount by remember { mutableStateOf("") }
    var moneyComment by remember { mutableStateOf("") }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(goal.title, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text("${(progress * 100).toInt()}% — ${NumberFormat.getCurrencyInstance(java.util.Locale("ru","RU")).format(goal.currentAmount)}")
            Spacer(Modifier.height(8.dp))
            Button(onClick = { showMoney = true }, modifier = Modifier.fillMaxWidth()) { Text("💰 Пополнить") }
        }
    }

    if (showMoney) {
        AlertDialog(
            onDismissRequest = { showMoney = false },
            title = { Text("Пополнить цель") },
            text = {
                Column {
                    OutlinedTextField(moneyAmount, { moneyAmount = it }, label = { Text("Сумма") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(moneyComment, { moneyComment = it }, label = { Text("Комментарий") })
                }
            },
            confirmButton = { Button(onClick = { viewModel.addMoney(goal.id, true, moneyAmount, moneyComment); moneyAmount = ""; moneyComment = ""; showMoney = false }) { Text("ОК") } },
            dismissButton = { TextButton(onClick = { showMoney = false }) { Text("Отмена") } }
        )
    }
}

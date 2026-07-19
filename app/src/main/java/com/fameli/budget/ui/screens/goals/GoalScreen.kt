package com.fameli.budget.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.local.entity.GoalEntity
import com.fameli.budget.data.local.entity.GoalTransactionEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalScreen(viewModel: GoalViewModel = hiltViewModel()) {
    val goals by viewModel.goals.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val showAddMoneyDialog by viewModel.showAddMoneyDialog.collectAsState()

    // Локальные состояния для полей ввода
    var goalTitle by remember { mutableStateOf("") }
    var goalDesc by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("") }
    var moneyAmount by remember { mutableStateOf("") }
    var moneyComment by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("🎯 Цели", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }

            if (goals.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎯", style = MaterialTheme.typography.displayMedium)
                            Text("Нет целей")
                        }
                    }
                }
            }

            items(goals) { goal ->
                val transactions by viewModel.getTransactions(goal.id).collectAsState(emptyList())
                GoalCard(
                    goal = goal,
                    transactions = transactions,
                    onAddMoney = { viewModel.showAddMoney(goal.id) },
                    onDelete = { viewModel.deleteGoal(goal) },
                    onToggle = { viewModel.toggleComplete(goal) }
                )
            }

            item { Spacer(Modifier.height(72.dp)) }
        }

        FloatingActionButton(
            onClick = { viewModel.showAdd() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Filled.Add, null) }
    }

    // Диалог создания цели
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                viewModel.hideAdd()
                goalTitle = ""; goalDesc = ""; goalTarget = ""
            },
            title = { Text("Новая цель") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = goalDesc,
                        onValueChange = { goalDesc = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = goalTarget,
                        onValueChange = { goalTarget = it },
                        label = { Text("Сумма (₽)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addGoal(goalTitle, goalDesc, goalTarget)
                        goalTitle = ""; goalDesc = ""; goalTarget = ""
                    },
                    enabled = goalTitle.isNotBlank() && goalTarget.toDoubleOrNull() != null
                ) { Text("Создать") }
            },
            dismissButton = { 
                TextButton(onClick = { viewModel.hideAdd(); goalTitle = ""; goalDesc = ""; goalTarget = "" }) { Text("Отмена") } 
            }
        )
    }

    // Диалог пополнения
    if (showAddMoneyDialog != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddMoney(); moneyAmount = ""; moneyComment = "" },
            title = { Text("Пополнить цель") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = moneyAmount,
                        onValueChange = { moneyAmount = it },
                        label = { Text("Сумма (₽)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = moneyComment,
                        onValueChange = { moneyComment = it },
                        label = { Text("Комментарий") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addMoney(showAddMoneyDialog!!, true, moneyAmount, moneyComment)
                        moneyAmount = ""; moneyComment = ""
                    },
                    enabled = moneyAmount.toDoubleOrNull() != null
                ) { Text("Пополнить") }
            },
            dismissButton = { TextButton(onClick = { viewModel.hideAddMoney(); moneyAmount = ""; moneyComment = "" }) { Text("Отмена") } }
        )
    }
}

@Composable
fun GoalCard(goal: GoalEntity, transactions: List<GoalTransactionEntity>, onAddMoney: () -> Unit, onDelete: () -> Unit, onToggle: () -> Unit) {
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(goal.title, fontWeight = FontWeight.Bold)
                    if (goal.description.isNotBlank()) Text(goal.description, style = MaterialTheme.typography.bodySmall)
                }
                Row {
                    Checkbox(checked = goal.isCompleted, onCheckedChange = { onToggle() })
                    IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text("${(progress * 100).toInt()}% — ${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(goal.currentAmount)} / ${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(goal.targetAmount)}")
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAddMoney, modifier = Modifier.weight(1f)) { Text("💰 Пополнить") }
                OutlinedButton(onClick = onAddMoney, modifier = Modifier.weight(1f)) { Text("💸 Снять") }
            }
            if (transactions.isNotEmpty()) {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                transactions.takeLast(5).forEach { txn ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        Text(if (txn.amount > 0) "+${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(txn.amount)}" else NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(txn.amount), color = if (txn.amount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodySmall)
                        Text("${txn.userName}: ${txn.comment}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

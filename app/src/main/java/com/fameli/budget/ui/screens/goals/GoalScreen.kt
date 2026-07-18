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
                            Text("Нет целей", style = MaterialTheme.typography.bodyLarge)
                            Text("Создайте первую цель для накопления", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            items(goals) { goal ->
                GoalCard(
                    goal = goal,
                    onAddMoney = { viewModel.showAddMoney(goal.id) },
                    onDelete = { viewModel.deleteGoal(goal) },
                    onToggle = { viewModel.toggleComplete(goal) },
                    viewModel = viewModel
                )
            }

            item { Spacer(Modifier.height(72.dp)) }
        }

        FloatingActionButton(
            onClick = { viewModel.showAdd() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, "Добавить цель")
        }
    }

    // Диалог создания цели
    if (showAddDialog) {
        AddGoalDialog(viewModel)
    }

    // Диалог пополнения/снятия
    if (showAddMoneyDialog != null) {
        AddMoneyDialog(viewModel, showAddMoneyDialog!!)
    }
}

@Composable
fun GoalCard(
    goal: GoalEntity,
    onAddMoney: () -> Unit,
    onDelete: () -> Unit,
    onToggle: () -> Unit,
    viewModel: GoalViewModel
) {
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val transactions by viewModel.goalDao.getTransactions(goal.id).collectAsState(initial = emptyList())

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(goal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (goal.description.isNotBlank()) {
                        Text(goal.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row {
                    Checkbox(checked = goal.isCompleted, onCheckedChange = { onToggle() })
                    IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, "Удалить", tint = MaterialTheme.colorScheme.error) }
                }
            }

            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                Text(
                    "${NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(goal.currentAmount)} / ${NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = onAddMoney, modifier = Modifier.weight(1f)) { Text("💰 Пополнить") }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onAddMoney, modifier = Modifier.weight(1f)) { Text("💸 Снять") }
            }

            // Чат/история пополнений
            if (transactions.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("История", style = MaterialTheme.typography.titleSmall)
                transactions.takeLast(5).forEach { txn ->
                    TransactionComment(txn)
                }
            }
        }
    }
}

@Composable
fun TransactionComment(txn: GoalTransactionEntity) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            if (txn.amount > 0) "+${NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(txn.amount)}"
            else NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(txn.amount),
            color = if (txn.amount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(txn.comment, style = MaterialTheme.typography.bodySmall)
            Text(
                "${txn.userName} • ${SimpleDateFormat("dd.MM HH:mm", Locale("ru")).format(Date(txn.timestamp))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(viewModel: GoalViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.hideAdd() },
        title = { Text("Новая цель") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.newGoalTitle.value,
                    onValueChange = { viewModel.newGoalTitle.value = it },
                    label = { Text("Название (например: Отпуск, Машина)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.newGoalDesc.value,
                    onValueChange = { viewModel.newGoalDesc.value = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.newGoalTarget.value,
                    onValueChange = { viewModel.newGoalTarget.value = it },
                    label = { Text("Сумма цели (₽)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.addGoal() }, enabled = viewModel.newGoalTitle.value.isNotBlank() && viewModel.newGoalTarget.value.toDoubleOrNull() != null) {
                Text("Создать")
            }
        },
        dismissButton = { TextButton(onClick = { viewModel.hideAdd() }) { Text("Отмена") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoneyDialog(viewModel: GoalViewModel, goalId: Long) {
    AlertDialog(
        onDismissRequest = { viewModel.hideAddMoney() },
        title = { Text("Пополнить цель") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.addAmount.value,
                    onValueChange = { viewModel.addAmount.value = it },
                    label = { Text("Сумма (₽)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = viewModel.addComment.value,
                    onValueChange = { viewModel.addComment.value = it },
                    label = { Text("Комментарий (обязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.addMoney(goalId, true) },
                enabled = viewModel.addAmount.value.toDoubleOrNull() != null && viewModel.addComment.value.isNotBlank()
            ) { Text("Пополнить") }
        },
        dismissButton = { TextButton(onClick = { viewModel.hideAddMoney() }) { Text("Отмена") } }
    )
}

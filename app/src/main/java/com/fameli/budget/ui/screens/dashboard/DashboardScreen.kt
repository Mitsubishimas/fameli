package com.fameli.budget.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.local.entity.TransactionEntity
import com.fameli.budget.ui.screens.planner.PlannerViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    plannerVM: PlannerViewModel = hiltViewModel()
) {
    val balance by viewModel.balance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val monthTasks by plannerVM.monthTasks.collectAsState()
    val selectedDate by plannerVM.selectedDate.collectAsState()
    val tasks by plannerVM.tasks.collectAsState()

    var showTransactions by remember { mutableStateOf<String?>(null) } // "INCOME" / "EXPENSE" / null

    val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthLabel = SimpleDateFormat("LLLL yyyy", Locale("ru")).format(Date(selectedDate))

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        item { Text(monthLabel.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }

        // Сводка
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Баланс: ${format(balance.totalIncome - balance.totalExpense)}",
                        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(
                            modifier = Modifier.clickable { showTransactions = "INCOME" }
                        ) {
                            Text("Доходы", style = MaterialTheme.typography.labelSmall)
                            Text("+${format(balance.totalIncome)}",
                                color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                        Column(
                            modifier = Modifier.clickable { showTransactions = "EXPENSE" },
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("Расходы", style = MaterialTheme.typography.labelSmall)
                            Text("-${format(balance.totalExpense)}",
                                color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Календарь
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс").forEach { day ->
                            Text(day, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    val firstDay = Calendar.getInstance().apply { timeInMillis = selectedDate; set(Calendar.DAY_OF_MONTH, 1) }
                    val startOffset = (firstDay.get(Calendar.DAY_OF_WEEK) + 5) % 7

                    var dayCounter = 1
                    for (week in 0 until 6) {
                        Row(Modifier.fillMaxWidth()) {
                            for (dayOfWeek in 0 until 7) {
                                if (week == 0 && dayOfWeek < startOffset) {
                                    Spacer(Modifier.weight(1f).height(44.dp))
                                } else if (dayCounter <= daysInMonth) {
                                    val currentDay = dayCounter
                                    val dayCal = Calendar.getInstance().apply {
                                        timeInMillis = selectedDate
                                        set(Calendar.DAY_OF_MONTH, currentDay)
                                        set(Calendar.HOUR_OF_DAY, 12)
                                    }
                                    val hasTasks = monthTasks.any { task ->
                                        val tc = Calendar.getInstance().apply { timeInMillis = task.date }
                                        tc.get(Calendar.DAY_OF_MONTH) == currentDay && tc.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                                    }
                                    val isSelected = cal.get(Calendar.DAY_OF_MONTH) == currentDay

                                    Box(
                                        modifier = Modifier.weight(1f).height(44.dp).padding(2.dp).clip(CircleShape)
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else if (hasTasks) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                            .clickable { plannerVM.setSelectedDate(dayCal.timeInMillis) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("$currentDay", color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified)
                                            if (hasTasks) Box(Modifier.size(5.dp).clip(CircleShape).background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary))
                                        }
                                    }
                                    dayCounter++
                                } else {
                                    Spacer(Modifier.weight(1f).height(44.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Задачи
        item { Text("Задачи: ${SimpleDateFormat("dd.MM.yyyy", Locale("ru")).format(Date(selectedDate))}", fontWeight = FontWeight.Bold) }
        if (tasks.isEmpty()) { item { Card(Modifier.fillMaxWidth()) { Text("Нет задач", modifier = Modifier.padding(16.dp)) } } }
        items(tasks) { task ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = task.isCompleted, onCheckedChange = { plannerVM.toggleComplete(task) })
                    Column(Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.Medium)
                        Text("${task.time} | ${task.createdBy}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }

    // Диалог транзакций по типу
    if (showTransactions != null) {
        val filtered = transactions.filter { it.type == showTransactions }
        AlertDialog(
            onDismissRequest = { showTransactions = null },
            title = { Text(if (showTransactions == "INCOME") "Доходы" else "Расходы") },
            text = {
                LazyColumn(Modifier.height(400.dp)) {
                    if (filtered.isEmpty()) {
                        item { Text("Нет транзакций") }
                    }
                    items(filtered) { txn ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(txn.note.ifBlank { txn.categoryName.ifBlank { "—" } }, fontWeight = FontWeight.Medium)
                                Text(txn.categoryName, style = MaterialTheme.typography.bodySmall)
                                Text("${SimpleDateFormat("dd.MM HH:mm", Locale("ru")).format(Date(txn.date))} | ${format(txn.amount)}",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { showTransactions = null }) { Text("Закрыть") } }
        )
    }
}

private fun format(amount: Double): String = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(amount)

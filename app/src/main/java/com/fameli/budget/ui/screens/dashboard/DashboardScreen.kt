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
import com.fameli.budget.data.local.entity.TaskEntity
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
    val monthTasks by plannerVM.monthTasks.collectAsState()
    val selectedDate by plannerVM.selectedDate.collectAsState()
    val tasks by plannerVM.tasks.collectAsState()

    val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthLabel = SimpleDateFormat("LLLL yyyy", Locale("ru")).format(Date(selectedDate))

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        // Текущий месяц
        item {
            Text(monthLabel.capitalize(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        // Финансовая сводка
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Баланс: ${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalIncome - balance.totalExpense)}",
                        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Доходы", style = MaterialTheme.typography.labelSmall)
                            Text("+${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalIncome)}",
                                color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Расходы", style = MaterialTheme.typography.labelSmall)
                            Text("-${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalExpense)}",
                                color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // КАЛЕНДАРЬ
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс").forEach { day ->
                            Text(day, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
                                        val taskCal = Calendar.getInstance().apply { timeInMillis = task.date }
                                        taskCal.get(Calendar.DAY_OF_MONTH) == currentDay && taskCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                                    }

                                    val isSelected = cal.get(Calendar.DAY_OF_MONTH) == currentDay

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else if (hasTasks) MaterialTheme.colorScheme.primaryContainer
                                                else Color.Transparent
                                            )
                                            .clickable { plannerVM.setSelectedDate(dayCal.timeInMillis) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("$currentDay",
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
                                                style = MaterialTheme.typography.bodySmall)
                                            if (hasTasks) {
                                                Box(Modifier.size(5.dp).clip(CircleShape).background(
                                                    if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                                ))
                                            }
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

        // Задачи на выбранную дату
        item {
            Text("Задачи: ${SimpleDateFormat("dd.MM.yyyy", Locale("ru")).format(Date(selectedDate))}",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (tasks.isEmpty()) {
            item { Card(Modifier.fillMaxWidth()) { Text("Нет задач на эту дату", modifier = Modifier.padding(16.dp)) } }
        }

        items(tasks) { task ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = task.isCompleted, onCheckedChange = { plannerVM.toggleComplete(task) })
                    Column(Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.Medium)
                        Text("🕐 ${task.time} | 👤 ${task.createdBy}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

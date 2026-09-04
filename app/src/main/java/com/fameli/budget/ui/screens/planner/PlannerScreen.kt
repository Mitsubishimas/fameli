package com.fameli.budget.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.fameli.budget.ui.screens.family.FamilyViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(viewModel: PlannerViewModel, showAddDialog: Boolean, onDismiss: () -> Unit) {
    val tasks by viewModel.tasks.collectAsState()
    val monthTasks by viewModel.monthTasks.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val familyVM: FamilyViewModel = hiltViewModel()
    val isSyncing by familyVM.isSyncing.collectAsState()
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isSyncing),
        onRefresh = { familyVM.forceSync() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            
            // КАЛЕНДАРЬ
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("📅 ${SimpleDateFormat("LLLL yyyy", Locale("ru")).format(Date(selectedDate))}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        
                        Row(Modifier.fillMaxWidth()) {
                            listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс").forEach { day ->
                                Text(day, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        val firstDay = Calendar.getInstance().apply { timeInMillis = selectedDate; set(Calendar.DAY_OF_MONTH, 1) }
                        val startOffset = (firstDay.get(Calendar.DAY_OF_WEEK) + 5) % 7
                        
                        var dayCounter = 1
                        for (week in 0 until 6) {
                            Row(Modifier.fillMaxWidth()) {
                                for (dayOfWeek in 0 until 7) {
                                    if (week == 0 && dayOfWeek < startOffset) {
                                        Spacer(Modifier.weight(1f).height(40.dp))
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
                                            modifier = Modifier.weight(1f).height(40.dp).padding(2.dp).clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else if (hasTasks) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                                .clickable { viewModel.setSelectedDate(dayCal.timeInMillis) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("$currentDay", color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified, style = MaterialTheme.typography.bodySmall)
                                                if (hasTasks) Box(Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                                            }
                                        }
                                        dayCounter++
                                    } else {
                                        Spacer(Modifier.weight(1f).height(40.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Задачи
            item { Text("Задачи: ${SimpleDateFormat("dd.MM.yyyy", Locale("ru")).format(Date(selectedDate))}", fontWeight = FontWeight.Bold) }
            if (tasks.isEmpty()) {
                item { Card(Modifier.fillMaxWidth()) { Text("Нет задач", modifier = Modifier.padding(24.dp)) } }
            }
            items(tasks) { task ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = task.isCompleted, onCheckedChange = { viewModel.toggleComplete(task) })
                        Column(Modifier.weight(1f)) {
                            Text(task.title, fontWeight = FontWeight.Medium)
                            if (task.description.isNotBlank()) {
                                Text(task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("${task.time} | ${task.createdBy}", style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(onClick = { viewModel.deleteTask(task) }) { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Новая задача") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(taskTitle, { taskTitle = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(taskDesc, { taskDesc = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth())
                    OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("🕐 ${viewModel.newTaskTime.value}") }
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("📅 ${SimpleDateFormat("dd.MM", Locale("ru")).format(Date(selectedDate))}") }
                }
            },
            confirmButton = { Button(onClick = { viewModel.addTask(taskTitle, taskDesc); taskTitle = ""; taskDesc = ""; onDismiss() }, enabled = taskTitle.isNotBlank()) { Text("Добавить") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
        )
    }

    if (showDatePicker) {
        val dp = rememberDatePickerState(viewModel.selectedDate.value)
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { dp.selectedDateMillis?.let { viewModel.setSelectedDate(it) }; showDatePicker = false }) { Text("OK") } }) { DatePicker(dp) }
    }

    if (showTimePicker) {
        val tp = rememberTimePickerState()
        AlertDialog(onDismissRequest = { showTimePicker = false }, title = { Text("Время") }, text = { TimePicker(tp) }, confirmButton = { Button(onClick = { viewModel.newTaskTime.value = "${tp.hour}:${tp.minute.toString().padStart(2, '0')}"; showTimePicker = false }) { Text("OK") } })
    }
}

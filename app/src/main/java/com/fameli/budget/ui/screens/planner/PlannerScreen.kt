package com.fameli.budget.ui.screens.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fameli.budget.data.local.entity.TaskEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(viewModel: PlannerViewModel, showAddDialog: Boolean, onDismiss: () -> Unit) {
    val tasks by viewModel.tasks.collectAsState()
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("📅 Планы", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        if (tasks.isEmpty()) {
            item { Card(Modifier.fillMaxWidth()) { Text("Нет задач", modifier = Modifier.padding(24.dp)) } }
        }
        items(tasks) { task ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = task.isCompleted, onCheckedChange = { viewModel.toggleComplete(task) })
                    Column(Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.Medium)
                        Text("🕐 ${task.time} | 👤 ${task.createdBy}", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { viewModel.deleteTask(task) }) { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
        item { Spacer(Modifier.height(72.dp)) }
    }

    // Диалог добавления
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Новая задача") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(taskTitle, { taskTitle = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(taskDesc, { taskDesc = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth())
                    OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("🕐 ${viewModel.newTaskTime.value}")
                    }
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("📅 ${SimpleDateFormat("dd.MM.yyyy", Locale("ru")).format(Date(viewModel.selectedDate.value))}")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addTask(taskTitle, taskDesc)
                    taskTitle = ""; taskDesc = ""
                    onDismiss()
                }, enabled = taskTitle.isNotBlank()) { Text("Добавить") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
        )
    }

    // DatePicker
    if (showDatePicker) {
        val dp = rememberDatePickerState(initialSelectedDateMillis = viewModel.selectedDate.value)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { dp.selectedDateMillis?.let { viewModel.setSelectedDate(it) }; showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Отмена") } }
        ) { DatePicker(dp) }
    }

    // TimePicker
    if (showTimePicker) {
        val tp = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Выберите время") },
            text = { TimePicker(tp) },
            confirmButton = {
                Button(onClick = {
                    viewModel.newTaskTime.value = "${tp.hour}:${tp.minute.toString().padStart(2, '0')}"
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Отмена") } }
        )
    }
}

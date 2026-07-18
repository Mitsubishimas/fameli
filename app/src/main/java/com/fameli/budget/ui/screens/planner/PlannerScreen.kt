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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.fameli.budget.data.local.entity.TaskEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel,
    showAddDialog: Boolean = false,
    onDismissDialog: () -> Unit = {}
) {
    val tasks by viewModel.tasks.collectAsState()
    val internalShowDialog by viewModel.showAddDialog.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    val showDialog = showAddDialog || internalShowDialog
    var showDatePicker by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📅 Планировщик", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    
                    // Фильтр по дате
                    TextButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.DateRange, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            SimpleDateFormat("dd.MM.yyyy", Locale("ru")).format(Date(selectedDate)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (tasks.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📝", style = MaterialTheme.typography.displayMedium)
                            Text("Нет задач на выбранную дату", style = MaterialTheme.typography.bodyLarge)
                            Text("Нажмите + чтобы добавить", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            val grouped = tasks.groupBy { 
                SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date(it.date))
            }

            grouped.forEach { (date, dayTasks) ->
                item {
                    Text(date, style = MaterialTheme.typography.titleSmall, 
                         color = MaterialTheme.colorScheme.primary,
                         modifier = Modifier.padding(top = 8.dp))
                }
                items(dayTasks) { task ->
                    TaskCard(task, 
                        onToggle = { viewModel.toggleComplete(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }

            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    // DatePicker диалог
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.setSelectedDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Диалог добавления задачи
    if (showDialog) {
        AddTaskDialog(
            viewModel = viewModel,
            onDismiss = {
                viewModel.hideAdd()
                onDismissDialog()
            }
        )
    }
}

@Composable
fun TaskCard(task: TaskEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    fontWeight = if (!task.isCompleted) FontWeight.Medium else FontWeight.Normal
                )
                if (task.description.isNotBlank()) {
                    Text(task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("🕐 ${task.time}", style = MaterialTheme.typography.labelSmall)
                    Text("👤 ${task.createdBy}", style = MaterialTheme.typography.labelSmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(viewModel: PlannerViewModel, onDismiss: () -> Unit) {
    val title by viewModel.newTaskTitle.collectAsState()
    val desc by viewModel.newTaskDesc.collectAsState()
    val time by viewModel.newTaskTime.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая задача") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.newTaskTitle.value = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { viewModel.newTaskDesc.value = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { viewModel.newTaskTime.value = it },
                    label = { Text("Время (14:30)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Выбор даты
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.DateRange, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Дата: ${SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date(selectedDate))}")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.addTask()
                onDismiss()
            }, enabled = title.isNotBlank()) {
                Text("Добавить")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )

    // DatePicker внутри диалога
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.setSelectedDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

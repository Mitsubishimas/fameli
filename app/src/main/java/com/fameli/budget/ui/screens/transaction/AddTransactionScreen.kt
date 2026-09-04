package com.fameli.budget.ui.screens.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, viewModel: AddTransactionViewModel = hiltViewModel()) {
    val amount by viewModel.amount.collectAsState()
    val note by viewModel.note.collectAsState()
    val isExpense by viewModel.isExpense.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val showAddCategory by viewModel.showAddCategory.collectAsState()
    val newCategoryName by viewModel.newCategoryName.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить транзакцию") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.toggleType(true) }, modifier = Modifier.weight(1f)) { Text("Расход") }
                Button(onClick = { viewModel.toggleType(false) }, modifier = Modifier.weight(1f)) { Text("Доход") }
            }

            OutlinedTextField(amount, { viewModel.updateAmount(it) }, label = { Text("Сумма") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())

            Text("Категория")
            OutlinedButton(onClick = { showCategoryDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedCategory?.name ?: "Выберите категорию")
            }

            OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text("📅 ${SimpleDateFormat("dd.MM.yyyy", Locale("ru")).format(Date(selectedDate))}")
            }

            OutlinedTextField(note, { viewModel.updateNote(it) }, label = { Text("Заметка") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.weight(1f))
            Button(onClick = { viewModel.save(); navController.navigateUp() },
                modifier = Modifier.fillMaxWidth(), enabled = amount.isNotBlank() && selectedCategory != null) {
                Text("Сохранить")
            }
        }
    }

    // Диалог выбора категории
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Выберите категорию") },
            text = {
                LazyColumn(Modifier.height(350.dp)) {
                    items(categories) { cat ->
                        Text(
                            "${cat.icon} ${cat.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectCategory(cat); showCategoryDialog = false }
                                .padding(14.dp),
                            color = if (selectedCategory?.id == cat.id) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                    }
                    item {
                        Divider()
                        Text(
                            "＋ Добавить категорию",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryDialog = false; viewModel.showAddCategoryDialog() }
                                .padding(14.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = { Button(onClick = { showCategoryDialog = false }) { Text("Закрыть") } }
        )
    }

    // Диалог добавления категории
    if (showAddCategory) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddCategoryDialog() },
            title = { Text("Новая категория") },
            text = {
                OutlinedTextField(newCategoryName, { viewModel.updateNewCategoryName(it) }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                Button(onClick = { viewModel.addCategory() }, enabled = newCategoryName.isNotBlank()) { Text("Добавить") }
            },
            dismissButton = { TextButton(onClick = { viewModel.hideAddCategoryDialog() }) { Text("Отмена") } }
        )
    }

    if (showDatePicker) {
        val dp = rememberDatePickerState(selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { dp.selectedDateMillis?.let { viewModel.setDate(it) }; showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Отмена") } }
        ) { DatePicker(dp) }
    }
}

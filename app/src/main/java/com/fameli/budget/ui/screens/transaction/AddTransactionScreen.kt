package com.fameli.budget.ui.screens.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, viewModel: AddTransactionViewModel = hiltViewModel()) {
    val amount by viewModel.amount.collectAsState()
    val note by viewModel.note.collectAsState()
    val isExpense by viewModel.isExpense.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selected by viewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Добавить") },
                navigationIcon = { 
                    IconButton(onClick = { navController.navigateUp() }) { 
                        Icon(Icons.Filled.ArrowBack, "Назад") 
                    } 
                }
            ) 
        }
    ) { p ->
        Column(
            Modifier.fillMaxSize().padding(p).padding(16.dp), 
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Тип операции
            Text("Тип операции", style = MaterialTheme.typography.titleSmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.toggleType(true) },
                    modifier = Modifier.weight(1f),
                    colors = if (isExpense) ButtonDefaults.buttonColors() 
                             else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Расход")
                }
                Button(
                    onClick = { viewModel.toggleType(false) },
                    modifier = Modifier.weight(1f),
                    colors = if (!isExpense) ButtonDefaults.buttonColors() 
                             else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Доход")
                }
            }

            // Сумма
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.updateAmount(it) },
                label = { Text("Сумма (₽)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Категории
            Text("Категория", style = MaterialTheme.typography.titleSmall)
            if (categories.isEmpty()) {
                Text("Нет категорий. Добавьте в разделе 'Категории'", 
                     style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.error)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { c ->
                        SuggestionChip(
                            onClick = { viewModel.selectCategory(c) },
                            label = { Text("${c.icon} ${c.name}") },
                            colors = if (selected?.id == c.id) 
                                SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            else SuggestionChipDefaults.suggestionChipColors()
                        )
                    }
                }
            }

            // Заметка
            OutlinedTextField(
                value = note,
                onValueChange = { viewModel.updateNote(it) },
                label = { Text("Заметка (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            // Кнопка сохранить
            Button(
                onClick = { viewModel.save(); navController.navigateUp() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0 && selected != null
            ) {
                Text("Сохранить", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

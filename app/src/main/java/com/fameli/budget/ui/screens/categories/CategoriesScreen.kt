package com.fameli.budget.ui.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType

@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel = hiltViewModel()) {
    val expenses by viewModel.expenseCategories.collectAsState()
    val incomes by viewModel.incomeCategories.collectAsState()
    val showAdd by viewModel.showAddDialog.collectAsState()
    val newName by viewModel.newName.collectAsState()
    val newIcon by viewModel.newIcon.collectAsState()
    val newType by viewModel.newType.collectAsState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("Расходы", style = MaterialTheme.typography.titleMedium) }
            items(expenses) { c -> CategoryItem(c, viewModel) }
            item { Text("Доходы", style = MaterialTheme.typography.titleMedium) }
            items(incomes) { c -> CategoryItem(c, viewModel) }
            item { Spacer(Modifier.height(72.dp)) }
        }

        // КНОПКА ДОБАВЛЕНИЯ
        FloatingActionButton(
            onClick = { viewModel.showAdd() },
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, "Добавить категорию")
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAdd() },
            title = { Text("Новая категория") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(newName, { viewModel.updateName(it) }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(newType == CategoryType.EXPENSE, { viewModel.updateType(CategoryType.EXPENSE) }, label = { Text("Расход") })
                        FilterChip(newType == CategoryType.INCOME, { viewModel.updateType(CategoryType.INCOME) }, label = { Text("Доход") })
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("🍔","🚗","🏠","🎮","💊","👕","💼","🎁","📈","💰").forEach { emoji ->
                            FilterChip(newIcon == emoji, { viewModel.updateIcon(emoji) }, label = { Text(emoji) })
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { viewModel.addCategory() }, enabled = newName.isNotBlank()) { Text("Сохранить") } },
            dismissButton = { TextButton(onClick = { viewModel.hideAdd() }) { Text("Отмена") } }
        )
    }
}

@Composable
fun CategoryItem(c: CategoryEntity, viewModel: CategoriesViewModel) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${c.icon} ${c.name}")
            IconButton(onClick = { viewModel.delete(c) }) { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
        }
    }
}

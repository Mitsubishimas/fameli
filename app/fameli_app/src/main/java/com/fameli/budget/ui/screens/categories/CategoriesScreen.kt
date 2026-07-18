package com.fameli.budget.ui.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.local.entity.CategoryEntity

@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel = hiltViewModel()) {
    val expenses by viewModel.expenseCategories.collectAsState()
    val incomes by viewModel.incomeCategories.collectAsState()
    val isAdding by viewModel.isAdding.collectAsState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("Расходы", style = MaterialTheme.typography.titleMedium) }
            items(expenses) { c -> CategoryItem(c) { viewModel.delete(c) } }
            item { Text("Доходы", style = MaterialTheme.typography.titleMedium) }
            items(incomes) { c -> CategoryItem(c) { viewModel.delete(c) } }
        }
        FloatingActionButton(onClick = { viewModel.showAdd() }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Icon(Icons.Filled.Add, contentDescription = "Добавить")
        }
    }

    if (isAdding) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAdd() },
            title = { Text("Новая категория") },
            text = {
                Column {
                    OutlinedTextField(viewModel.newName.value, { viewModel.updateName(it) }, label = { Text("Название") })
                    Row { listOf("💰","🍔","🚗").forEach { FilterChip(viewModel.newIcon.value == it, { viewModel.updateIcon(it) }, label = { Text(it) }) } }
                }
            },
            confirmButton = { Button({ viewModel.addCategory() }) { Text("Добавить") } },
            dismissButton = { TextButton({ viewModel.hideAdd() }) { Text("Отмена") } }
        )
    }
}

@Composable
fun CategoryItem(c: CategoryEntity, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("${c.icon} ${c.name}")
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
        }
    }
}

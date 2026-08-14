package com.fameli.budget.ui.screens.shopping

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
import com.fameli.budget.data.local.entity.ShoppingItemEntity

@Composable
fun ShoppingScreen(
    viewModel: ShoppingViewModel,
    showAddDialog: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val items by viewModel.items.collectAsState()
    val internalShowAdd by viewModel.showAddDialog.collectAsState()
    val newName by viewModel.newItemName.collectAsState()
    val showDialog = showAddDialog || internalShowAdd

    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("Список покупок", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
            if (items.isEmpty()) {
                item { Card(Modifier.fillMaxWidth()) { Text("Список пуст", modifier = Modifier.padding(24.dp)) } }
            }
            items(items) { item -> ShoppingItemCard(item, viewModel) }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAdd(); onDismiss() },
            title = { Text("Новая покупка") },
            text = { OutlinedTextField(newName, { viewModel.newItemName.value = it }, label = { Text("Что купить?") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { Button(onClick = { viewModel.addItem(); onDismiss() }, enabled = newName.isNotBlank()) { Text("Добавить") } },
            dismissButton = { TextButton(onClick = { viewModel.hideAdd(); onDismiss() }) { Text("Отмена") } }
        )
    }
}

@Composable
fun ShoppingItemCard(item: ShoppingItemEntity, viewModel: ShoppingViewModel) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = item.isPurchased, onCheckedChange = { viewModel.togglePurchased(item) })
            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(item.name, textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None)
                Text("by ${item.createdByName}", style = MaterialTheme.typography.labelSmall)
                if (item.isPurchased) Text("Купил: ${item.purchasedByName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { viewModel.deleteItem(item) }) { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
        }
    }
}

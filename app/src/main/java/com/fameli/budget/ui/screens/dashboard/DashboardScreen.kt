package com.fameli.budget.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.local.entity.TransactionEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val balance by viewModel.balance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    var selectedTxn by remember { mutableStateOf<TransactionEntity?>(null) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Баланс", style = MaterialTheme.typography.labelLarge)
                    Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalIncome - balance.totalExpense), style = MaterialTheme.typography.headlineLarge)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalIncome), color = MaterialTheme.colorScheme.primary)
                        Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalExpense), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
        items(transactions.take(50)) { txn ->
            Card(Modifier.fillMaxWidth().clickable { selectedTxn = txn }) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(txn.note ?: "—")
                        Text(SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru")).format(Date(txn.date)), style = MaterialTheme.typography.bodySmall)
                    }
                    Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(txn.amount))
                }
            }
        }
    }

    // Диалог редактирования/удаления
    selectedTxn?.let { txn ->
        AlertDialog(
            onDismissRequest = { selectedTxn = null },
            title = { Text("Транзакция") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Сумма: ${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(txn.amount)}")
                    Text("Заметка: ${txn.note ?: "—"}")
                    Text("Дата: ${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru")).format(Date(txn.date))}")
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { viewModel.deleteTransaction(txn); selectedTxn = null }) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                    Button(onClick = { selectedTxn = null }) { Text("Закрыть") }
                }
            },
            dismissButton = { TextButton(onClick = { selectedTxn = null }) { Text("Отмена") } }
        )
    }
}

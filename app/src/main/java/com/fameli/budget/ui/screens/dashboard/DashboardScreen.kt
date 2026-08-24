package com.fameli.budget.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.local.entity.TransactionEntity
import com.fameli.budget.ui.screens.family.FamilyViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val balance by viewModel.balance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val familyVM: FamilyViewModel = hiltViewModel()
    var selectedTxn by remember { mutableStateOf<TransactionEntity?>(null) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Баланс")
                    Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalIncome - balance.totalExpense), style = MaterialTheme.typography.headlineLarge)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalIncome), color = Color(0xFF2E7D32))
                        Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalExpense), color = Color(0xFFC62828))
                    }
                }
            }
        }

        item {
            Button(onClick = { familyVM.forceSync() }, modifier = Modifier.fillMaxWidth()) {
                Text("Обновить")
            }
        }

        items(transactions.take(100)) { txn ->
            val isIncome = txn.type == "INCOME"
            val txnColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
            val sign = if (isIncome) "+" else "-"

            Card(Modifier.fillMaxWidth().clickable { selectedTxn = txn }) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(txn.note.ifBlank { txn.categoryName.ifBlank { "—" } }, style = MaterialTheme.typography.bodyLarge)
                        if (txn.categoryName.isNotBlank()) {
                            Text(txn.categoryName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru")).format(Date(txn.date)), style = MaterialTheme.typography.bodySmall)
                    }
                    Text("$sign${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(txn.amount)}", color = txnColor, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    selectedTxn?.let { txn ->
        AlertDialog(
            onDismissRequest = { selectedTxn = null },
            title = { Text(txn.categoryName.ifBlank { "Транзакция" }) },
            text = {
                Column {
                    Text("Сумма: ${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(txn.amount)}")
                    Text("Тип: ${if (txn.type == "INCOME") "Доход" else "Расход"}")
                    Text("Категория: ${txn.categoryName.ifBlank { "—" }}")
                    Text("Заметка: ${txn.note.ifBlank { "—" }}")
                }
            },
            confirmButton = {
                Row {
                    TextButton(onClick = { viewModel.deleteTransaction(txn); selectedTxn = null }) {
                        Text("Удалить", color = Color(0xFFC62828))
                    }
                    Button(onClick = { selectedTxn = null }) { Text("Закрыть") }
                }
            },
            dismissButton = { TextButton(onClick = { selectedTxn = null }) { Text("Отмена") } }
        )
    }
}

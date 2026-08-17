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
    val categories by viewModel.categories.collectAsState()
    val familyVM: FamilyViewModel = hiltViewModel()
    val isSyncing by familyVM.isSyncing.collectAsState()
    val message by familyVM.message.collectAsState()
    var selectedTxn by remember { mutableStateOf<TransactionEntity?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Баланс")
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(balance.totalIncome - balance.totalExpense),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(balance.totalIncome),
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(balance.totalExpense),
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = { familyVM.forceSync() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSyncing
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Синхронизация...")
                } else {
                    Icon(imageVector = Icons.Filled.Sync, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Обновить")
                }
            }
        }

        val msg = message
        if (msg != null) {
            item {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(transactions.take(50)) { txn ->
            val cat = categories.find { it.id == txn.categoryId }
            val isIncome = cat?.type?.name == "INCOME" || txn.type == "INCOME"
            val txnColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
            val sign = if (isIncome) "+" else "-"

            Card(modifier = Modifier.fillMaxWidth().clickable { selectedTxn = txn }) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = txn.note.ifBlank { cat?.name ?: "—" },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru")).format(Date(txn.date)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "$sign${NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(txn.amount)}",
                        color = txnColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    selectedTxn?.let { txn ->
        AlertDialog(
            onDismissRequest = { selectedTxn = null },
            title = { Text("Транзакция") },
            text = {
                Column {
                    Text("Сумма: ${NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(txn.amount)}")
                    Text("Тип: ${txn.type}")
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

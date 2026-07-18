package com.fameli.budget.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val balance by viewModel.balance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

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
        items(transactions.take(20)) { t ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(t.note ?: "—")
                    Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(t.amount))
                }
            }
        }
    }
}

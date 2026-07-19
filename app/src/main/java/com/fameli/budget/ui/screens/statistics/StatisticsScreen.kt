package com.fameli.budget.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel = hiltViewModel()) {
    val expenses by viewModel.expenses.collectAsState()
    val incomes by viewModel.incomes.collectAsState()
    val balance by viewModel.balance.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Статистика", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        // Баланс
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Баланс за месяц", style = MaterialTheme.typography.labelLarge)
                    Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalIncome - balance.totalExpense),
                        style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column {
                            Text("Доходы", style = MaterialTheme.typography.labelSmall)
                            Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalIncome), color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text("Расходы", style = MaterialTheme.typography.labelSmall)
                            Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(balance.totalExpense), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // Расходы по категориям
        item { Text("Расходы по категориям", style = MaterialTheme.typography.titleMedium) }
        if (expenses.isEmpty()) {
            item { Text("Нет данных", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(expenses) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName)
                    Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(e.total), fontWeight = FontWeight.Medium)
                }
            }
        }

        // Доходы
        item { Text("Доходы по категориям", style = MaterialTheme.typography.titleMedium) }
        if (incomes.isEmpty()) {
            item { Text("Нет данных", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(incomes) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName)
                    Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(e.total), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

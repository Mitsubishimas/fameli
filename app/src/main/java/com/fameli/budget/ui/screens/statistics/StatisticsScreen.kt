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
    val period by viewModel.period.collectAsState()
    val totalExpense = expenses.sumOf { it.total }
    val totalIncome = incomes.sumOf { it.total }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Аналитика", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("День" to "day", "Месяц" to "month", "Год" to "year").forEach { (label, value) ->
                    FilterChip(selected = period == value, onClick = { viewModel.setPeriod(value) }, label = { Text(label) })
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Доходы: +${format(totalIncome)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Расходы: -${format(totalExpense)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    Text("Баланс: ${format(totalIncome - totalExpense)}", fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Text("Доходы", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
        if (incomes.isEmpty()) { item { Text("Нет доходов") } }
        items(incomes) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName)
                    Text(format(e.total))
                }
            }
        }

        item { Text("Расходы", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) }
        if (expenses.isEmpty()) { item { Text("Нет расходов") } }
        items(expenses) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName)
                    Text(format(e.total))
                }
            }
        }
    }
}

private fun format(amount: Double): String = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(amount)

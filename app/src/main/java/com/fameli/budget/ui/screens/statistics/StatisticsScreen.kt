package com.fameli.budget.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.pie.pieChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
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

        // Итого
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Итого за период", fontWeight = FontWeight.Bold)
                    Text("Доходы: +${format(totalIncome)}", color = MaterialTheme.colorScheme.primary)
                    Text("Расходы: -${format(totalExpense)}", color = MaterialTheme.colorScheme.error)
                    Text("Баланс: ${format(totalIncome - totalExpense)}", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Круговая диаграмма расходов
        if (expenses.isNotEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Расходы по категориям", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        val pieModel = entryModelOf(*expenses.map { it.total.toFloat() }.toTypedArray())
                        Chart(
                            chart = pieChart(),
                            model = pieModel,
                            modifier = Modifier.fillMaxWidth().height(180.dp)
                        )
                    }
                }
            }
        }

        // Столбчатая диаграмма доходов
        if (incomes.isNotEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Доходы по категориям", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        val maxIncome = incomes.maxOf { it.total }
                        val colModel = entryModelOf(*incomes.map { (it.total / maxIncome).toFloat() }.toTypedArray())
                        Chart(
                            chart = columnChart(),
                            model = colModel,
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(),
                            modifier = Modifier.fillMaxWidth().height(180.dp)
                        )
                    }
                }
            }
        }

        // Список расходов
        item { Text("Расходы", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) }
        items(expenses) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName)
                    Text(format(e.total))
                }
            }
        }

        // Список доходов
        item { Text("Доходы", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
        items(incomes) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName)
                    Text(format(e.total))
                }
            }
        }
    }
}

private fun format(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(amount)
}

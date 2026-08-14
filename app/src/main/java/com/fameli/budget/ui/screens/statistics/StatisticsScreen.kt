package com.fameli.budget.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
                    Text("Итого", fontWeight = FontWeight.Bold)
                    Text("Доходы: +${format(totalIncome)}", color = MaterialTheme.colorScheme.primary)
                    Text("Расходы: -${format(totalExpense)}", color = MaterialTheme.colorScheme.error)
                    Text("Баланс: ${format(totalIncome - totalExpense)}", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (expenses.isNotEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Расходы по категориям", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        val maxVal = expenses.maxOf { it.total }
                        expenses.forEach { e ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Text(e.categoryName, modifier = Modifier.width(100.dp), style = MaterialTheme.typography.bodySmall)
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE0E0E0))
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth((e.total / maxVal).toFloat().coerceIn(0.05f, 1f))
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.error)
                                    )
                                }
                                Text(format(e.total), modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        item { Text("Расходы", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) }
        items(expenses) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName); Text(format(e.total))
                }
            }
        }

        item { Text("Доходы", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
        items(incomes) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName); Text(format(e.total))
                }
            }
        }
    }
}

private fun format(amount: Double): String = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(amount)

package com.fameli.budget.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel = hiltViewModel()) {
    val expenses by viewModel.expenses.collectAsState()
    val period by viewModel.period.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("📊 Аналитика", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("День" to "day", "Месяц" to "month", "Год" to "year").forEach { (label, value) ->
                    FilterChip(
                        selected = period == value,
                        onClick = { viewModel.setPeriod(value) },
                        label = { Text(label) }
                    )
                }
            }
        }

        if (expenses.isEmpty()) {
            item { Card(Modifier.fillMaxWidth()) { Text("Нет данных за период", modifier = Modifier.padding(16.dp)) } }
        }

        items(expenses) { e ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.categoryName)
                    Text(NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(e.total))
                }
            }
        }
    }
}

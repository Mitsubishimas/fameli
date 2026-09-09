package com.fameli.budget.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.ui.screens.family.FamilyViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.NumberFormat
import java.time.YearMonth
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    familyVM: FamilyViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val incomes by viewModel.incomes.collectAsState()
    val period by viewModel.period.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val isSyncing by familyVM.isSyncing.collectAsState()
    val totalExpense = expenses.sumOf { it.total }
    val totalIncome = incomes.sumOf { it.total }
    var showMonthPicker by remember { mutableStateOf(false) }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isSyncing),
        onRefresh = { familyVM.forceSync() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Аналитика", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(selected = period == "month", onClick = { viewModel.setPeriod("month") }, label = { Text("Месяц") })
                    FilterChip(selected = period == "year", onClick = { viewModel.setPeriod("year") }, label = { Text("Год") })
                    
                    // Кнопка выбора месяца
                    if (period == "month") {
                        OutlinedButton(onClick = { showMonthPicker = true }) {
                            Icon(Icons.Filled.CalendarMonth, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(selectedMonth.toString())
                        }
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

            if (expenses.isNotEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Расходы по категориям", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            PieChart(expenses, Modifier.size(200.dp).align(Alignment.CenterHorizontally))
                            Spacer(Modifier.height(16.dp))
                            val colors = pieColors
                            expenses.forEachIndexed { index, e ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(12.dp).background(colors[index % colors.size], CircleShape))
                                    Spacer(Modifier.width(8.dp))
                                    Text(e.categoryName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                    Text(format(e.total), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                    Text(" (${(e.total / totalExpense * 100).toInt()}%)", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            if (incomes.isNotEmpty()) {
                item { Text("Доходы", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                items(incomes) { e ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(e.categoryName)
                            Text(format(e.total))
                        }
                    }
                }
            }
        }
    }

    // Диалог выбора месяца
    if (showMonthPicker) {
        val months = listOf("Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь")
        val currentYear = YearMonth.now().year
        AlertDialog(
            onDismissRequest = { showMonthPicker = false },
            title = { Text("Выберите месяц") },
            text = {
                LazyColumn(Modifier.height(300.dp)) {
                    items(months.size) { index ->
                        Text(
                            months[index],
                            modifier = Modifier.fillMaxWidth().clickable { 
                                viewModel.setMonth(YearMonth.of(currentYear, index + 1))
                                showMonthPicker = false
                            }.padding(14.dp),
                            fontWeight = if (index + 1 == selectedMonth.monthValue) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            },
            confirmButton = { Button(onClick = { showMonthPicker = false }) { Text("Закрыть") } }
        )
    }
}

val pieColors = listOf(
    Color(0xFF5B9EFF), Color(0xFFFF6B7A), Color(0xFF4CAF50), Color(0xFFFF9800),
    Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFFE91E63), Color(0xFF8BC34A),
    Color(0xFFFF5722), Color(0xFF795548), Color(0xFF607D8B), Color(0xFF3F51B5)
)

@Composable
fun PieChart(data: List<com.fameli.budget.data.model.CategoryExpense>, modifier: Modifier = Modifier) {
    val total = data.sumOf { it.total }
    if (total <= 0) return
    Canvas(modifier = modifier) {
        val strokeWidth = 30.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val radius = diameter / 2
        val center = Offset(size.width / 2, size.height / 2)
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(diameter, diameter)
        var startAngle = -90f
        data.forEachIndexed { index, item ->
            val sweepAngle = (item.total / total * 360).toFloat()
            drawArc(
                color = pieColors[index % pieColors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweepAngle
        }
    }
}

private fun format(amount: Double): String = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(amount)

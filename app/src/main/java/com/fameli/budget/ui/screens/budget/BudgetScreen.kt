package com.fameli.budget.ui.screens.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.model.BudgetProgress
import com.fameli.budget.ui.screens.family.FamilyViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = hiltViewModel(),
    showAddDialog: Boolean = false,
    onDismissAdd: () -> Unit = {}
) {
    val progress by viewModel.budgetProgress.collectAsState()
    val totalLimit by viewModel.totalLimit.collectAsState()
    val totalSpent by viewModel.totalSpent.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val categories by viewModel.expenseCategories.collectAsState()
    val familyVM: FamilyViewModel = hiltViewModel()
    val isSyncing by familyVM.isSyncing.collectAsState()

    var showMonthPicker by remember { mutableStateOf(false) }
    var internalShowAdd by remember { mutableStateOf(false) }
    val showAdd = showAddDialog || internalShowAdd

    var selectedCategory by remember { mutableStateOf("") }
    var limitText by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isSyncing),
        onRefresh = { familyVM.forceSync() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Бюджет", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    OutlinedButton(onClick = { showMonthPicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(formatMonth(selectedMonth))
                    }
                }
            }

            item {
                val remaining = totalLimit - totalSpent
                val exceeded = totalSpent > totalLimit && totalLimit > 0
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                    containerColor = if (exceeded) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                )) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("План: ${formatMoney(totalLimit)}", fontWeight = FontWeight.Bold)
                        Text("Потрачено: ${formatMoney(totalSpent)}")
                        Text(
                            if (exceeded) "Превышение: ${formatMoney(totalSpent - totalLimit)}"
                            else "Осталось: ${formatMoney(remaining.coerceAtLeast(0.0))}",
                            fontWeight = FontWeight.Bold,
                            color = if (exceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (progress.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Нет планов бюджета на ${formatMonth(selectedMonth)}")
                            Spacer(Modifier.height(8.dp))
                            Text("Нажмите + и укажите категорию и лимит", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(progress, key = { it.budgetId }) { item ->
                    BudgetCard(item, onDelete = { viewModel.deleteBudget(item.budgetId) }, onEdit = { viewModel.updateLimit(item.budgetId, it) })
                }
            }

            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { internalShowAdd = false; onDismissAdd(); selectedCategory = ""; limitText = "" },
            title = { Text("План бюджета") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Месяц: ${formatMonth(selectedMonth)}", style = MaterialTheme.typography.bodyMedium)
                    ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                        OutlinedTextField(
                            value = selectedCategory, onValueChange = {}, readOnly = true,
                            label = { Text("Категория расходов") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                            if (categories.isEmpty()) {
                                DropdownMenuItem(text = { Text("Нет категорий") }, onClick = { categoryExpanded = false })
                            } else categories.forEach { cat ->
                                DropdownMenuItem(text = { Text("${cat.icon} ${cat.name}") }, onClick = { selectedCategory = cat.name; categoryExpanded = false })
                            }
                        }
                    }
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*[.,]?\\d{0,2}$"))) limitText = it },
                        label = { Text("Лимит, ₽") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.addBudget(selectedCategory, limitText); internalShowAdd = false; onDismissAdd(); selectedCategory = ""; limitText = "" },
                    enabled = selectedCategory.isNotBlank() && limitText.isNotBlank()) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { internalShowAdd = false; onDismissAdd(); selectedCategory = ""; limitText = "" }) { Text("Отмена") } }
        )
    }

    if (showMonthPicker) {
        val months = listOf("Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь")
        val now = YearMonth.now()
        AlertDialog(
            onDismissRequest = { showMonthPicker = false },
            title = { Text("Выберите месяц") },
            text = {
                LazyColumn(Modifier.height(320.dp)) {
                    items((-1..11).map { now.plusMonths(it.toLong()) }) { ym ->
                        val sel = ym == selectedMonth
                        Text("${months[ym.monthValue - 1]} ${ym.year}",
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.setMonth(ym); showMonthPicker = false }.padding(14.dp),
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                            color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showMonthPicker = false }) { Text("Закрыть") } }
        )
    }
}

@Composable
private fun BudgetCard(item: BudgetProgress, onDelete: () -> Unit, onEdit: (String) -> Unit) {
    var showEdit by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(item.limitAmount.toInt().toString()) }
    val barColor = when {
        item.isExceeded -> MaterialTheme.colorScheme.error
        item.isNearLimit -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
        containerColor = if (item.isExceeded) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
    )) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(item.categoryName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row {
                    IconButton(onClick = { editText = if (item.limitAmount % 1.0 == 0.0) item.limitAmount.toInt().toString() else item.limitAmount.toString(); showEdit = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Edit, "Изменить", Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, "Удалить", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            LinearProgressIndicator(progress = { item.progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp), color = barColor, trackColor = MaterialTheme.colorScheme.surfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${formatMoney(item.spent)} / ${formatMoney(item.limitAmount)}", style = MaterialTheme.typography.bodyMedium)
                Text("${(item.progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = barColor)
            }
            when {
                item.isExceeded -> Text("Превышение: ${formatMoney(item.overAmount)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                item.isNearLimit -> Text("Осталось: ${formatMoney(item.remaining)} (близко к лимиту)", color = Color(0xFFFF9800), style = MaterialTheme.typography.bodySmall)
                else -> Text("Осталось: ${formatMoney(item.remaining)}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    if (showEdit) {
        AlertDialog(
            onDismissRequest = { showEdit = false },
            title = { Text("Изменить лимит: ${item.categoryName}") },
            text = { OutlinedTextField(editText, { if (it.isEmpty() || it.matches(Regex("^\\d*[.,]?\\d{0,2}$"))) editText = it }, label = { Text("Лимит, ₽") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true) },
            confirmButton = { Button(onClick = { onEdit(editText); showEdit = false }) { Text("Сохранить") } },
            dismissButton = { TextButton(onClick = { showEdit = false }) { Text("Отмена") } }
        )
    }
}

private fun formatMoney(amount: Double): String = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).format(amount)

private fun formatMonth(month: YearMonth): String {
    val name = month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))
    return "${name.replaceFirstChar { it.uppercase() }} ${month.year}"
}

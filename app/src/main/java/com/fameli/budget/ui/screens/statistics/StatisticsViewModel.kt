package com.fameli.budget.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.TransactionDao
import com.fameli.budget.data.model.CategoryExpense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val dao: TransactionDao) : ViewModel() {
    
    // Текущий месяц по умолчанию
    val selectedMonth = MutableStateFlow(YearMonth.now())
    val period = MutableStateFlow("month")
    
    private fun getRange(month: YearMonth): Pair<Long, Long> {
        val start = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
        val end = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 - 1
        return start to end
    }

    val expenses: StateFlow<List<CategoryExpense>> = selectedMonth.flatMapLatest { month ->
        val (s, e) = getRange(month)
        dao.getCategorySums(s, e, "EXPENSE")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomes: StateFlow<List<CategoryExpense>> = selectedMonth.flatMapLatest { month ->
        val (s, e) = getRange(month)
        dao.getCategorySums(s, e, "INCOME")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setMonth(month: YearMonth) { selectedMonth.value = month }
    fun previousMonth() { selectedMonth.value = selectedMonth.value.minusMonths(1) }
    fun nextMonth() { selectedMonth.value = selectedMonth.value.plusMonths(1) }
    fun setPeriod(p: String) { period.value = p }
}

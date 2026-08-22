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
    
    val period = MutableStateFlow("month")
    val selectedMonth = MutableStateFlow(YearMonth.now())

    private fun getRange(p: String, month: YearMonth): Pair<Long, Long> {
        val now = LocalDate.now()
        return when (p) {
            "day" -> {
                val start = now.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
                start to start + 86400000 - 1
            }
            "month" -> {
                month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 to
                month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 - 1
            }
            else -> { // year
                val yearStart = now.withDayOfYear(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
                yearStart to yearStart + 365L * 86400000L - 1
            }
        }
    }

    val expenses: StateFlow<List<CategoryExpense>> = combine(period, selectedMonth) { p, m ->
        val (s, e) = getRange(p, m)
        s to e
    }.flatMapLatest { (s, e) ->
        dao.getCategorySums(s, e, "EXPENSE")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomes: StateFlow<List<CategoryExpense>> = combine(period, selectedMonth) { p, m ->
        val (s, e) = getRange(p, m)
        s to e
    }.flatMapLatest { (s, e) ->
        dao.getCategorySums(s, e, "INCOME")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setPeriod(p: String) { period.value = p }
    fun previousMonth() { selectedMonth.value = selectedMonth.value.minusMonths(1) }
    fun nextMonth() { selectedMonth.value = selectedMonth.value.plusMonths(1) }
}

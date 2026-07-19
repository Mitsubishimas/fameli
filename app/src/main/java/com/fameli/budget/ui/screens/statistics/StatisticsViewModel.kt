package com.fameli.budget.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.TransactionDao
import com.fameli.budget.data.model.CategoryExpense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val dao: TransactionDao) : ViewModel() {
    val period = MutableStateFlow("month")

    private fun getRange(p: String): Pair<Long, Long> {
        val now = LocalDate.now()
        return when (p) {
            "day" -> {
                val start = now.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
                start to start + 86400000 - 1
            }
            "month" -> {
                now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 to
                now.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 - 1
            }
            else -> {
                now.withDayOfYear(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 to
                now.plusYears(1).withDayOfYear(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 - 1
            }
        }
    }

    val expenses: StateFlow<List<CategoryExpense>> = period.flatMapLatest { p ->
        val (s, e) = getRange(p)
        dao.getCategorySums(s, e, "EXPENSE")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomes: StateFlow<List<CategoryExpense>> = period.flatMapLatest { p ->
        val (s, e) = getRange(p)
        dao.getCategorySums(s, e, "INCOME")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setPeriod(p: String) { period.value = p }
}

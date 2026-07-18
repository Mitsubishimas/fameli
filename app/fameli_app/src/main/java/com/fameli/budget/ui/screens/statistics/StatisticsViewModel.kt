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
    private val now = LocalDate.now()
    private val start = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
    private val end = now.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 - 1

    val expenses: StateFlow<List<CategoryExpense>> = dao.getCategorySums(start, end, "EXPENSE").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

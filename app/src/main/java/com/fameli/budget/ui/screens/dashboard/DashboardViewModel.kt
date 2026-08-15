package com.fameli.budget.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.TransactionDao
import com.fameli.budget.data.local.entity.TransactionEntity
import com.fameli.budget.data.model.MonthlyBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val dao: TransactionDao) : ViewModel() {
    private val now = LocalDate.now()
    private val start = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
    private val end = now.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000 - 1

    val balance: StateFlow<MonthlyBalance> = dao.getMonthlyBalance(start, end).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthlyBalance(0.0, 0.0))
    val transactions: StateFlow<List<TransactionEntity>> = dao.getBetween(start, end).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteTransaction(txn: TransactionEntity) = viewModelScope.launch {
        dao.softDelete(txn.localId)
    }
}

package com.fameli.budget.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.TransactionDao
import com.fameli.budget.data.local.entity.TransactionEntity
import com.fameli.budget.data.model.MonthlyBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val dao: TransactionDao) : ViewModel() {
    
    // ВСЕ транзакции
    val transactions: StateFlow<List<TransactionEntity>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Общий баланс — сумма всех доходов минус все расходы
    val balance: StateFlow<MonthlyBalance> = transactions
        .map { list ->
            val income = list.filter { it.type == "INCOME" }.sumOf { it.amount }
            val expense = list.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            MonthlyBalance(income, expense)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthlyBalance(0.0, 0.0))

    fun deleteTransaction(txn: TransactionEntity) = viewModelScope.launch {
        dao.softDelete(txn.localId)
    }
}

package com.fameli.budget.ui.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.BudgetDao
import com.fameli.budget.data.local.dao.CategoryDao
import com.fameli.budget.data.local.dao.TransactionDao
import com.fameli.budget.data.local.entity.BudgetEntity
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import com.fameli.budget.data.model.BudgetProgress
import com.fameli.budget.data.model.CategoryExpense
import com.fameli.budget.data.repository.FamilySyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val syncRepository: FamilySyncRepository
) : ViewModel() {

    val selectedMonth = MutableStateFlow(YearMonth.now())

    val expenseCategories: StateFlow<List<CategoryEntity>> =
        categoryDao.getByType(CategoryType.EXPENSE)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgetProgress: StateFlow<List<BudgetProgress>> =
        selectedMonth.flatMapLatest { month ->
            val monthKey = month.toString()
            val (start, end) = monthRangeMillis(month)
            combine(
                budgetDao.getForMonth(monthKey),
                transactionDao.getCategorySums(start, end, "EXPENSE")
            ) { budgets, expenses -> buildProgressList(budgets, expenses, monthKey) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalLimit: StateFlow<Double> =
        budgetProgress.map { it.sumOf { b -> b.limitAmount } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalSpent: StateFlow<Double> =
        budgetProgress.map { it.sumOf { b -> b.spent } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setMonth(month: YearMonth) { selectedMonth.value = month }

    fun addBudget(categoryName: String, limitStr: String) = viewModelScope.launch {
        val limit = limitStr.replace(',', '.').toDoubleOrNull() ?: return@launch
        if (limit <= 0 || categoryName.isBlank()) return@launch
        val monthKey = selectedMonth.value.toString()
        val existing = budgetDao.getByMonthAndCategory(monthKey, categoryName)
        val entity = if (existing != null) {
            val u = existing.copy(limitAmount = limit, lastModified = System.currentTimeMillis(), isDeleted = false)
            budgetDao.update(u); u
        } else {
            val c = BudgetEntity(
                cloudId = UUID.randomUUID().toString(),
                categoryName = categoryName,
                limitAmount = limit,
                month = monthKey,
                lastModified = System.currentTimeMillis()
            )
            val id = budgetDao.insert(c)
            c.copy(id = id)
        }
        syncRepository.pushBudget(entity)
    }

    fun updateLimit(budgetId: Long, limitStr: String) = viewModelScope.launch {
        val limit = limitStr.replace(',', '.').toDoubleOrNull() ?: return@launch
        if (limit <= 0) return@launch
        val existing = budgetDao.getById(budgetId) ?: return@launch
        val u = existing.copy(limitAmount = limit, lastModified = System.currentTimeMillis())
        budgetDao.update(u)
        syncRepository.pushBudget(u)
    }

    fun deleteBudget(budgetId: Long) = viewModelScope.launch {
        val existing = budgetDao.getById(budgetId) ?: return@launch
        budgetDao.softDelete(budgetId)
        syncRepository.pushBudget(existing.copy(isDeleted = true, lastModified = System.currentTimeMillis()))
    }

    private fun buildProgressList(
        budgets: List<BudgetEntity>,
        expenses: List<CategoryExpense>,
        monthKey: String
    ): List<BudgetProgress> {
        val spentByCat = expenses.associate { it.categoryName to it.total }
        return budgets.map { b ->
            val spent = spentByCat[b.categoryName] ?: 0.0
            val remaining = b.limitAmount - spent
            val progress = if (b.limitAmount > 0) (spent / b.limitAmount).toFloat() else 0f
            val exceeded = spent > b.limitAmount
            BudgetProgress(
                budgetId = b.id,
                categoryName = b.categoryName,
                limitAmount = b.limitAmount,
                month = monthKey,
                spent = spent,
                remaining = remaining,
                progress = progress,
                isExceeded = exceeded,
                overAmount = if (exceeded) spent - b.limitAmount else 0.0,
                alertThreshold = b.alertThreshold
            )
        }.sortedWith(compareByDescending<BudgetProgress> { it.isExceeded }.thenByDescending { it.progress })
    }

    private fun monthRangeMillis(month: YearMonth): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val start = month.atDay(1).atStartOfDay(zone).toEpochSecond() * 1000
        val end = month.plusMonths(1).atDay(1).atStartOfDay(zone).toEpochSecond() * 1000 - 1
        return start to end
    }
}

package com.fameli.budget.ui.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.CategoryDao
import com.fameli.budget.data.local.dao.TransactionDao
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import com.fameli.budget.data.local.entity.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : ViewModel() {
    val amount = MutableStateFlow("")
    val note = MutableStateFlow("")
    val isExpense = MutableStateFlow(true)
    val selectedCategory = MutableStateFlow<CategoryEntity?>(null)
    val selectedDate = MutableStateFlow(System.currentTimeMillis())
    val showAddCategory = MutableStateFlow(false)
    val newCategoryName = MutableStateFlow("")
    val syncMessage = MutableStateFlow("")

    val categories: StateFlow<List<CategoryEntity>> = isExpense.flatMapLatest { exp ->
        categoryDao.getByType(if (exp) CategoryType.EXPENSE else CategoryType.INCOME)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateAmount(v: String) { if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) amount.value = v }
    fun updateNote(v: String) { note.value = v }
    fun toggleType(exp: Boolean) { isExpense.value = exp; selectedCategory.value = null }
    fun selectCategory(c: CategoryEntity) { selectedCategory.value = c }
    fun setDate(date: Long) { selectedDate.value = date }
    fun showAddCategoryDialog() { showAddCategory.value = true; newCategoryName.value = "" }
    fun hideAddCategoryDialog() { showAddCategory.value = false }
    fun updateNewCategoryName(v: String) { newCategoryName.value = v }

    fun addCategory() = viewModelScope.launch {
        val name = newCategoryName.value.trim()
        if (name.isBlank()) return@launch
        
        // Проверка на дубликат (любой регистр)
        val existing = categoryDao.getAll().first()
        val duplicate = existing.any { it.name.equals(name, ignoreCase = true) }
        
        if (duplicate) {
            syncMessage.value = "Категория уже существует!"
            return@launch
        }
        
        val cat = CategoryEntity(
            cloudId = UUID.randomUUID().toString(),
            name = name,
            type = if (isExpense.value) CategoryType.EXPENSE else CategoryType.INCOME,
            icon = "💰"
        )
        categoryDao.insert(cat)
        selectedCategory.value = cat
        showAddCategory.value = false
        syncMessage.value = "Категория добавлена"
    }

    fun save() = viewModelScope.launch {
        val a = amount.value.toDoubleOrNull() ?: return@launch
        val c = selectedCategory.value ?: return@launch
        val txn = TransactionEntity(
            cloudId = UUID.randomUUID().toString(),
            type = if (isExpense.value) "EXPENSE" else "INCOME",
            amount = a,
            categoryId = c.id,
            categoryName = c.name,
            note = note.value,
            date = selectedDate.value,
            lastModified = System.currentTimeMillis()
        )
        transactionDao.insert(txn)
        amount.value = ""; note.value = ""; selectedCategory.value = null
        selectedDate.value = System.currentTimeMillis()
    }
}

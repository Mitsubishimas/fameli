package com.fameli.budget.ui.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.data.repository.FamilyManager
import com.fameli.budget.data.repository.FamilySyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val familyRepo: FamilySyncRepository,
    private val familyManager: FamilyManager
) : ViewModel() {
    val amount = MutableStateFlow("")
    val note = MutableStateFlow("")
    val isExpense = MutableStateFlow(true)
    val selectedCategory = MutableStateFlow<CategoryEntity?>(null)
    val syncMessage = MutableStateFlow("")

    val categories: StateFlow<List<CategoryEntity>> = isExpense.flatMapLatest { exp ->
        categoryDao.getByType(if (exp) CategoryType.EXPENSE else CategoryType.INCOME)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateAmount(v: String) { if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) amount.value = v }
    fun updateNote(v: String) { note.value = v }
    fun toggleType(exp: Boolean) { isExpense.value = exp; selectedCategory.value = null }
    fun selectCategory(c: CategoryEntity) { selectedCategory.value = c }

    fun save() = viewModelScope.launch {
        val a = amount.value.toDoubleOrNull() ?: return@launch
        val c = selectedCategory.value ?: return@launch
        val type = if (isExpense.value) "EXPENSE" else "INCOME"

        val txn = TransactionEntity(
            cloudId = UUID.randomUUID().toString(),
            type = type,
            amount = a,
            categoryId = c.id,
            categoryName = c.name,
            note = note.value,
            date = System.currentTimeMillis()
        )
        
        // 1. Сохраняем локально
        transactionDao.insert(txn)
        amount.value = ""; note.value = ""; selectedCategory.value = null

        // 2. Отправляем в облако
        val fid = familyManager.currentFamilyId
        if (fid != null) {
            syncMessage.value = "Отправка..."
            launch(Dispatchers.IO) {
                familyRepo.syncTransaction(txn)
                syncMessage.value = "✅ Отправлено"
            }
        } else {
            syncMessage.value = "❌ Нет семьи"
        }
    }
}

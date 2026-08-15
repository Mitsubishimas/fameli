package com.fameli.budget.ui.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.*
import com.fameli.budget.data.local.entity.*
import com.fameli.budget.data.repository.FamilySyncRepository
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val familyRepo: FamilySyncRepository
) : ViewModel() {
    val amount = MutableStateFlow("")
    val note = MutableStateFlow("")
    val isExpense = MutableStateFlow(true)
    val selectedCategory = MutableStateFlow<CategoryEntity?>(null)

    val categories: StateFlow<List<CategoryEntity>> = isExpense.flatMapLatest { exp ->
        categoryDao.getByType(if (exp) CategoryType.EXPENSE else CategoryType.INCOME)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateAmount(v: String) { if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) amount.value = v }
    fun updateNote(v: String) { note.value = v }
    fun toggleType(exp: Boolean) { isExpense.value = exp; selectedCategory.value = null }
    fun selectCategory(c: CategoryEntity) { selectedCategory.value = c }

    fun save(familyId: String?) = viewModelScope.launch {
        val a = amount.value.toDoubleOrNull() ?: return@launch
        val c = selectedCategory.value ?: return@launch
        val txn = TransactionEntity(
            cloudId = UUID.randomUUID().toString(),
            categoryId = c.id,
            amount = a,
            date = System.currentTimeMillis(),
            note = note.value.ifBlank { null }
        )
        transactionDao.insert(txn)
        
        // Отправляем в облако если есть семья
        if (familyId != null) {
            familyRepo.syncTransaction(familyId, txn)
        }
        
        amount.value = ""; note.value = ""; selectedCategory.value = null
    }
}

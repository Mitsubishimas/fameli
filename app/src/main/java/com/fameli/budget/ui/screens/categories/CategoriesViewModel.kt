package com.fameli.budget.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.CategoryDao
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val dao: CategoryDao) : ViewModel() {
    val expenseCategories = dao.getByType(CategoryType.EXPENSE).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val incomeCategories = dao.getByType(CategoryType.INCOME).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val isAdding = MutableStateFlow(false)
    val newName = MutableStateFlow("")
    val newIcon = MutableStateFlow("💰")

    fun showAdd() { isAdding.value = true }
    fun hideAdd() { isAdding.value = false }
    fun updateName(v: String) { newName.value = v }
    fun updateIcon(v: String) { newIcon.value = v }
    fun addCategory() = viewModelScope.launch {
        dao.insert(CategoryEntity(cloudId = UUID.randomUUID().toString(), name = newName.value, type = CategoryType.EXPENSE, icon = newIcon.value))
        hideAdd(); newName.value = ""
    }
    fun delete(c: CategoryEntity) = viewModelScope.launch { dao.softDelete(c.id) }
}

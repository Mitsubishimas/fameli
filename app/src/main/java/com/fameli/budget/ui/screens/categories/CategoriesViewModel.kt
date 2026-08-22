package com.fameli.budget.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.CategoryDao
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import com.fameli.budget.data.repository.FamilySyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val dao: CategoryDao,
    private val familyRepo: FamilySyncRepository
) : ViewModel() {

    val expenseCategories = dao.getByType(CategoryType.EXPENSE).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val incomeCategories = dao.getByType(CategoryType.INCOME).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val showAddDialog = MutableStateFlow(false)
    val newName = MutableStateFlow("")
    val newIcon = MutableStateFlow("💰")
    val newType = MutableStateFlow(CategoryType.EXPENSE)

    fun showAdd() { showAddDialog.value = true; newName.value = "" }
    fun hideAdd() { showAddDialog.value = false }
    fun updateName(v: String) { newName.value = v }
    fun updateIcon(v: String) { newIcon.value = v }
    fun updateType(t: CategoryType) { newType.value = t }

    fun addCategory() = viewModelScope.launch {
        val cat = CategoryEntity(
            cloudId = UUID.randomUUID().toString(),
            name = newName.value,
            type = newType.value,
            icon = newIcon.value
        )
        dao.insert(cat)
        // Синхронизация в фоне
        launch(Dispatchers.IO) { familyRepo.syncCategory(cat) }
        hideAdd()
    }

    fun delete(c: CategoryEntity) = viewModelScope.launch { dao.softDelete(c.id) }
}

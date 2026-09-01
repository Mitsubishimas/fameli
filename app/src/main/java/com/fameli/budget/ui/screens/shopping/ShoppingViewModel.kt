package com.fameli.budget.ui.screens.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.ShoppingDao
import com.fameli.budget.data.local.entity.ShoppingItemEntity
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingDao: ShoppingDao,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    val items: StateFlow<List<ShoppingItemEntity>> = shoppingDao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val showAddDialog = MutableStateFlow(false)
    val newItemName = MutableStateFlow("")

    fun showAdd() { showAddDialog.value = true; newItemName.value = "" }
    fun hideAdd() { showAddDialog.value = false }

    fun addItem() = viewModelScope.launch {
        val name = newItemName.value.trim()
        if (name.isBlank()) return@launch
        shoppingDao.insert(ShoppingItemEntity(
            cloudId = UUID.randomUUID().toString(),
            name = name,
            createdByUid = authRepository.getUserId() ?: "",
            createdByName = authRepository.getUserName(),
            lastModified = System.currentTimeMillis()
        ))
        hideAdd()
    }

    fun togglePurchased(item: ShoppingItemEntity) = viewModelScope.launch {
        val updated = if (item.isPurchased) {
            item.copy(isPurchased = false, purchasedByUid = "", purchasedByName = "", purchasedAt = 0, lastModified = System.currentTimeMillis())
        } else {
            item.copy(isPurchased = true, purchasedByUid = authRepository.getUserId() ?: "", purchasedByName = authRepository.getUserName(), purchasedAt = System.currentTimeMillis(), lastModified = System.currentTimeMillis())
        }
        shoppingDao.update(updated)
    }

    fun deleteItem(item: ShoppingItemEntity) = viewModelScope.launch { shoppingDao.softDelete(item.id) }
}

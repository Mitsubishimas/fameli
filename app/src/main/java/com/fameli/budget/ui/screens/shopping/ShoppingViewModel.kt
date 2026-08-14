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

    val items: StateFlow<List<ShoppingItemEntity>> = shoppingDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val showAddDialog = MutableStateFlow(false)
    val newItemName = MutableStateFlow("")

    fun showAdd() { showAddDialog.value = true; newItemName.value = "" }
    fun hideAdd() { showAddDialog.value = false; newItemName.value = "" }

    fun addItem() = viewModelScope.launch {
        val name = newItemName.value.trim()
        if (name.isBlank()) return@launch
        val userName = authRepository.getUserName()
        val uid = authRepository.getUserId() ?: ""
        shoppingDao.insert(ShoppingItemEntity(cloudId = UUID.randomUUID().toString(), name = name, createdByUid = uid, createdByName = userName))
        hideAdd()
    }

    fun togglePurchased(item: ShoppingItemEntity) = viewModelScope.launch {
        if (item.isPurchased) shoppingDao.markUnpurchased(item.id)
        else shoppingDao.markPurchased(item.id, authRepository.getUserId() ?: "", authRepository.getUserName())
    }

    fun deleteItem(item: ShoppingItemEntity) = viewModelScope.launch { shoppingDao.softDelete(item.id) }
}

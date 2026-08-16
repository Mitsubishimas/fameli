package com.fameli.budget.ui.screens.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.ShoppingDao
import com.fameli.budget.data.local.entity.ShoppingItemEntity
import com.fameli.budget.data.repository.FamilySyncRepository
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingDao: ShoppingDao,
    private val authRepository: FirebaseAuthRepository,
    private val familyRepo: FamilySyncRepository
) : ViewModel() {

    val items: StateFlow<List<ShoppingItemEntity>> = shoppingDao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val showAddDialog = MutableStateFlow(false)
    val newItemName = MutableStateFlow("")

    fun showAdd() { showAddDialog.value = true; newItemName.value = "" }
    fun hideAdd() { showAddDialog.value = false; newItemName.value = "" }

    fun addItem() = viewModelScope.launch {
        val name = newItemName.value.trim()
        if (name.isBlank()) return@launch
        val item = ShoppingItemEntity(cloudId = UUID.randomUUID().toString(), name = name, createdByUid = authRepository.getUserId() ?: "", createdByName = authRepository.getUserName())
        shoppingDao.insert(item)
        hideAdd()
        launch(Dispatchers.IO) { familyRepo.syncShoppingItem(item) }
    }

    fun togglePurchased(item: ShoppingItemEntity) = viewModelScope.launch {
        if (item.isPurchased) {
            shoppingDao.markUnpurchased(item.id)
            launch(Dispatchers.IO) { familyRepo.syncShoppingItem(item.copy(isPurchased = false)) }
        } else {
            shoppingDao.markPurchased(item.id, authRepository.getUserId() ?: "", authRepository.getUserName())
            val updated = item.copy(isPurchased = true, purchasedByUid = authRepository.getUserId() ?: "", purchasedByName = authRepository.getUserName())
            launch(Dispatchers.IO) { familyRepo.syncShoppingItem(updated) }
        }
    }

    fun deleteItem(item: ShoppingItemEntity) = viewModelScope.launch { shoppingDao.softDelete(item.id) }
}

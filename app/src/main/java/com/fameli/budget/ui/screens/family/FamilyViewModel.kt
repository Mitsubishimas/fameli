package com.fameli.budget.ui.screens.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.repository.FamilyManager
import com.fameli.budget.data.repository.FamilySyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyViewModel @Inject constructor(
    private val repo: FamilySyncRepository,
    private val familyManager: FamilyManager
) : ViewModel() {

    val familyId = MutableStateFlow(familyManager.currentFamilyId)
    val message = MutableStateFlow<String?>(null)
    val isLoading = MutableStateFlow(false)
    val isSyncing = MutableStateFlow(false)

    fun forceSync() = viewModelScope.launch(Dispatchers.IO) {
        val fid = familyManager.currentFamilyId ?: run {
            message.value = "Нет семьи"
            return@launch
        }
        isSyncing.value = true
        message.value = "Отправка..."
        repo.syncAllLocalToCloud().fold(
            onSuccess = {
                message.value = "Загрузка..."
                repo.syncAllFromCloud().fold(
                    onSuccess = { message.value = "Готово" },
                    onFailure = { e -> message.value = "Ошибка: ${e.message}" }
                )
            },
            onFailure = { e -> message.value = "Ошибка: ${e.message}" }
        )
        isSyncing.value = false
    }
}

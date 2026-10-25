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
    val isSyncing = MutableStateFlow(false)

    fun joinFamily(code: String) {
        if (code.isBlank()) return
        familyManager.currentFamilyId = code.trim()
        familyId.value = code.trim()
        message.value = "Присоединились: $code"
    }

    fun forceSync() = viewModelScope.launch(Dispatchers.IO) {
        familyManager.currentFamilyId ?: return@launch
        isSyncing.value = true

        repo.syncAllFromCloud().fold(
            onSuccess = {
                repo.syncAllLocalToCloud().fold(
                    onSuccess = { message.value = "Готово" },
                    onFailure = { e -> message.value = "Ошибка отправки: ${e.message}" }
                )
            },
            onFailure = { e -> message.value = "Ошибка загрузки: ${e.message}" }
        )
        isSyncing.value = false
    }
}

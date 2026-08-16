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

    fun createFamily() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            repo.createFamily("Моя семья").fold(
                onSuccess = { id ->
                    familyManager.currentFamilyId = id
                    familyId.value = id
                    repo.startListening()
                },
                onFailure = { e -> message.value = "Ошибка: ${e.message}" }
            )
            isLoading.value = false
        }
    }

    fun joinFamily(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            repo.joinFamily(code.trim()).fold(
                onSuccess = {
                    familyManager.currentFamilyId = code.trim()
                    familyId.value = code.trim()
                    repo.startListening()
                },
                onFailure = { e -> message.value = "Ошибка: ${e.message}" }
            )
            isLoading.value = false
        }
    }

    fun forceSync() {
        viewModelScope.launch(Dispatchers.IO) {
            val fid = familyManager.currentFamilyId ?: return@launch
            isSyncing.value = true
            message.value = "Отправка в облако..."
            repo.syncAllLocalToCloud().fold(
                onSuccess = {
                    message.value = "Отправлено. Загрузка из облака..."
                    repo.syncAllFromCloud().fold(
                        onSuccess = { message.value = "✅ Синхронизация завершена" },
                        onFailure = { e -> message.value = "Ошибка загрузки: ${e.message}" }
                    )
                },
                onFailure = { e -> message.value = "Ошибка отправки: ${e.message}" }
            )
            isSyncing.value = false
        }
    }

    fun leaveFamily() {
        repo.stopListening()
        familyManager.clear()
        familyId.value = null
        message.value = null
    }
}

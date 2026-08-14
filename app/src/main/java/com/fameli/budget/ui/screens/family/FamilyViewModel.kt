package com.fameli.budget.ui.screens.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.repository.FamilySyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyViewModel @Inject constructor(
    private val repo: FamilySyncRepository
) : ViewModel() {

    val familyId = MutableStateFlow<String?>(null)
    val message = MutableStateFlow<String?>(null)
    val isLoading = MutableStateFlow(false)
    val isSyncing = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            try {
                val families = repo.getMyFamilies()
                if (families.isNotEmpty()) {
                    val id = families.first()
                    familyId.value = id
                    repo.startListening(id)
                }
            } catch (_: Exception) {}
        }
    }

    fun createFamily() = viewModelScope.launch {
        isLoading.value = true
        repo.createFamily("Моя семья").fold(
            onSuccess = { id -> familyId.value = id; repo.startListening(id) },
            onFailure = { e -> message.value = "Ошибка: ${e.message}" }
        )
        isLoading.value = false
    }

    fun joinFamily(code: String) = viewModelScope.launch {
        isLoading.value = true
        repo.joinFamily(code.trim()).fold(
            onSuccess = { familyId.value = code.trim(); repo.startListening(code.trim()) },
            onFailure = { e -> message.value = "Ошибка: ${e.message}" }
        )
        isLoading.value = false
    }

    fun forceSync() = viewModelScope.launch {
        val id = familyId.value ?: return@launch
        isSyncing.value = true
        message.value = "Синхронизация..."
        
        // Сначала отправляем локальные данные в облако
        repo.forceSyncToCloud(id).fold(
            onSuccess = {
                // Потом загружаем из облака
                repo.forceSyncFromCloud(id).fold(
                    onSuccess = { message.value = "Синхронизация завершена ✅" },
                    onFailure = { e -> message.value = "Ошибка загрузки: ${e.message}" }
                )
            },
            onFailure = { e -> message.value = "Ошибка отправки: ${e.message}" }
        )
        isSyncing.value = false
    }

    fun leaveFamily() { repo.stopListening(); familyId.value = null; message.value = null }
}

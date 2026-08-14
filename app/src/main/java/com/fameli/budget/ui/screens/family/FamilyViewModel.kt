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

    init {
        viewModelScope.launch {
            try {
                val families = repo.getMyFamilies()
                if (families.isNotEmpty()) {
                    val id = families.first()
                    familyId.value = id
                    repo.startListening(id)
                    message.value = "Синхронизация активна"
                }
            } catch (e: Exception) {
                message.value = "Ошибка: ${e.message}"
            }
        }
    }

    fun createFamily() = viewModelScope.launch {
        isLoading.value = true
        message.value = null
        repo.createFamily("Моя семья").fold(
            onSuccess = { id ->
                familyId.value = id
                repo.startListening(id)
                message.value = "Семья создана"
            },
            onFailure = { e -> message.value = "Ошибка: ${e.message}" }
        )
        isLoading.value = false
    }

    fun joinFamily(code: String) = viewModelScope.launch {
        isLoading.value = true
        message.value = null
        repo.joinFamily(code.trim()).fold(
            onSuccess = {
                familyId.value = code.trim()
                repo.startListening(code.trim())
                message.value = "Вы в семье. Синхронизация включена."
            },
            onFailure = { e -> message.value = "Ошибка: ${e.message}" }
        )
        isLoading.value = false
    }

    fun leaveFamily() {
        repo.stopListening()
        familyId.value = null
        message.value = "Вы вышли из семьи"
    }
}

package com.fameli.budget.ui.screens.family

import android.util.Log
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
                    familyId.value = families.first()
                    repo.startListening(families.first())
                }
            } catch (e: Exception) {
                Log.e("FAMILY", "Init error: ${e.message}")
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
            onFailure = { e ->
                message.value = "Ошибка: ${e.message}"
                Log.e("FAMILY", "Create error: ${e.message}")
            }
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
                message.value = "Вы в семье"
            },
            onFailure = { e ->
                message.value = "Ошибка: ${e.message}"
            }
        )
        isLoading.value = false
    }

    fun leaveFamily() {
        repo.stopListening()
        familyId.value = null
        message.value = null
    }
}

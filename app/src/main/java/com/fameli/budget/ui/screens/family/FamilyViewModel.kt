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
    private val familySyncRepository: FamilySyncRepository
) : ViewModel() {

    val familyId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            val families = familySyncRepository.getMyFamilies()
            if (families.isNotEmpty()) {
                familyId.value = families.first()
                familySyncRepository.startListening(families.first())
            }
        }
    }

    fun createFamily() = viewModelScope.launch {
        val id = familySyncRepository.createFamily("Моя семья")
        familyId.value = id
        familySyncRepository.startListening(id)
    }

    fun joinFamily(code: String) = viewModelScope.launch {
        val success = familySyncRepository.joinFamily(code)
        if (success) {
            familyId.value = code
            familySyncRepository.startListening(code)
        }
    }

    fun leaveFamily() {
        familySyncRepository.stopListening()
        familyId.value = null
    }
}

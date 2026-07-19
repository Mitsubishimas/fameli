package com.fameli.budget.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.UpdateChecker
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class UpdateAvailable(val version: String) : UpdateStatus()
    object UpToDate : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    val updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val currentVersion: String = com.fameli.budget.BuildConfig.VERSION_NAME

    fun checkForUpdates() {
        updateStatus.value = UpdateStatus.Checking
        UpdateChecker.check(context, showToast = true)
        updateStatus.value = UpdateStatus.Idle
    }

    fun logout() = viewModelScope.launch { authRepository.signOut() }
}

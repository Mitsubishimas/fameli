package com.fameli.budget.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.fameli.budget.worker.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    val yandexToken: StateFlow<String?> = MutableStateFlow(null)
    val lastSync: StateFlow<Long> = MutableStateFlow(0)
    val isSyncing: StateFlow<Boolean> = MutableStateFlow(false)

    fun login() { /* OAuth Яндекс */ }
    fun logout() = viewModelScope.launch { authRepository.signOut() }
    fun syncNow() { SyncWorker.enqueue(context, "") }
    fun exportCsv() { /* TODO */ }
}

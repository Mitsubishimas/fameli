package com.fameli.budget.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.fameli.budget.worker.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext private val context: Context) : ViewModel() {
    val yandexToken: StateFlow<String?> = MutableStateFlow(null)
    fun login() { /* OAuth */ }
    fun logout() { }
    fun syncNow() { SyncWorker.enqueue(context, "") }
}

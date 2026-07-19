package com.fameli.budget.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.BuildConfig
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class UpdateAvailable(val version: String, val url: String) : UpdateStatus()
    object UpToDate : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    val updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val currentVersion: String = BuildConfig.VERSION_NAME

    fun checkForUpdates() = viewModelScope.launch {
        updateStatus.value = UpdateStatus.Checking
        try {
            val latestVersion = fetchLatestVersion()
            
            if (latestVersion == null) {
                updateStatus.value = UpdateStatus.Error("Сервер недоступен")
                return@launch
            }

            if (latestVersion != currentVersion) {
                updateStatus.value = UpdateStatus.UpdateAvailable(
                    version = latestVersion,
                    url = "https://github.com/Mitsubishimas/fameli/releases/latest"
                )
            } else {
                updateStatus.value = UpdateStatus.UpToDate
            }
        } catch (e: Exception) {
            updateStatus.value = UpdateStatus.Error("Ошибка: ${e.message}")
        }
    }

    private suspend fun fetchLatestVersion(): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://raw.githubusercontent.com/Mitsubishimas/fameli/main/version.txt")
            val text = url.readText(Charsets.UTF_8)
            text.trim().lines().firstOrNull()?.trim()
        } catch (e: Exception) {
            // Если GitHub недоступен — пробуем запасной URL
            try {
                val url2 = URL("https://raw.githubusercontent.com/Mitsubishimas/fameli/refs/heads/main/version.txt")
                url2.readText(Charsets.UTF_8).trim().lines().firstOrNull()?.trim()
            } catch (e2: Exception) {
                null
            }
        }
    }

    fun logout() = viewModelScope.launch { authRepository.signOut() }
}

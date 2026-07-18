package com.fameli.budget.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.BuildConfig
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.fameli.budget.worker.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
    val yandexToken: StateFlow<String?> = MutableStateFlow(null)
    val lastSync: StateFlow<Long> = MutableStateFlow(0)
    val isSyncing: StateFlow<Boolean> = MutableStateFlow(false)
    val updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)

    // Берём версию из BuildConfig (единственный источник правды)
    val currentVersion: String = BuildConfig.VERSION_NAME

    fun checkForUpdates() = viewModelScope.launch {
        updateStatus.value = UpdateStatus.Checking
        try {
            val latestVersion = fetchLatestVersion()
            if (latestVersion != null && latestVersion != currentVersion) {
                updateStatus.value = UpdateStatus.UpdateAvailable(
                    version = latestVersion,
                    url = "https://github.com/Mitsubishimas/fameli/releases/latest"
                )
            } else {
                updateStatus.value = UpdateStatus.UpToDate
            }
        } catch (e: Exception) {
            updateStatus.value = UpdateStatus.Error("Не удалось проверить обновления")
        }
    }

    private suspend fun fetchLatestVersion(): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/Mitsubishimas/fameli/releases/latest")
            val json = url.readText()
            val tagName = json.split("\"tag_name\":\"")[1].split("\"")[0]
            tagName.removePrefix("v")
        } catch (e: Exception) {
            null
        }
    }

    fun login() { }
    fun logout() = viewModelScope.launch { authRepository.signOut() }
    fun syncNow() { SyncWorker.enqueue(context, "") }
}

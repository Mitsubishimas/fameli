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
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class UpdateAvailable(val version: String, val url: String) : UpdateStatus()
    object UpToDate : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

data class GitHubRelease(
    val tag_name: String = "",
    val draft: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    val updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val currentVersion: String = BuildConfig.VERSION_NAME

    private val json = Json { ignoreUnknownKeys = true }

    fun checkForUpdates() = viewModelScope.launch {
        updateStatus.value = UpdateStatus.Checking
        try {
            // Пробуем GitHub API
            var latestVersion = fetchFromGitHub()
            
            // Если GitHub не ответил — пробуем альтернативный источник (простой текстовый файл)
            if (latestVersion == null) {
                latestVersion = fetchFromRawFile()
            }
            
            if (latestVersion == null) {
                // Если оба не сработали — проверяем закешированную версию
                latestVersion = getCachedVersion()
                if (latestVersion == null) {
                    updateStatus.value = UpdateStatus.Error("Не удалось проверить. Проверьте интернет.")
                    return@launch
                }
            } else {
                // Кешируем успешный результат
                saveCachedVersion(latestVersion)
            }

            if (isVersionNewer(latestVersion, currentVersion)) {
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

    private suspend fun fetchFromGitHub(): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/Mitsubishimas/fameli/releases/latest")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            connection.setRequestProperty("User-Agent", "Fameli-App")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode != 200) {
                // Лимит исчерпан или другая ошибка
                return@withContext null
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val responseText = reader.readText()
            reader.close()
            connection.disconnect()

            val release = json.decodeFromString<GitHubRelease>(responseText)
            
            if (release.draft || release.tag_name.isBlank()) return@withContext null
            
            release.tag_name.removePrefix("v")
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchFromRawFile(): String? = withContext(Dispatchers.IO) {
        try {
            // Читаем версию из raw-файла в репозитории
            val url = URL("https://raw.githubusercontent.com/Mitsubishimas/fameli/main/version.txt")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode != 200) return@withContext null

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val version = reader.readLine()?.trim()
            reader.close()
            connection.disconnect()

            version
        } catch (e: Exception) {
            null
        }
    }

    private fun saveCachedVersion(version: String) {
        context.getSharedPreferences("fameli_prefs", Context.MODE_PRIVATE)
            .edit().putString("latest_version", version).apply()
    }

    private fun getCachedVersion(): String? {
        return context.getSharedPreferences("fameli_prefs", Context.MODE_PRIVATE)
            .getString("latest_version", null)
    }

    private fun isVersionNewer(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        
        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }

    fun logout() = viewModelScope.launch { authRepository.signOut() }
}

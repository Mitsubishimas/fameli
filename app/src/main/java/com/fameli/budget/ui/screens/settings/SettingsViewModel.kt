package com.fameli.budget.ui.screens.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.BuildConfig
import com.fameli.budget.data.local.dao.CategoryDao
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.UUID
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
    private val authRepository: FirebaseAuthRepository,
    private val categoryDao: CategoryDao
) : ViewModel() {
    val updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val currentVersion: String = BuildConfig.VERSION_NAME

    fun addCategory(name: String, type: CategoryType, icon: String) = viewModelScope.launch {
        categoryDao.insert(CategoryEntity(cloudId = UUID.randomUUID().toString(), name = name, type = type, icon = icon))
    }

    fun checkForUpdates() = viewModelScope.launch {
        updateStatus.value = UpdateStatus.Checking
        try {
            val latestVersion = fetchLatestVersion()
            if (latestVersion != null && latestVersion != currentVersion) {
                updateStatus.value = UpdateStatus.UpdateAvailable(latestVersion, "https://github.com/Mitsubishimas/fameli/releases/latest")
            } else {
                updateStatus.value = UpdateStatus.UpToDate
            }
        } catch (e: Exception) {
            updateStatus.value = UpdateStatus.Error("Ошибка: ${e.message}")
        }
    }

    private suspend fun fetchLatestVersion(): String? = withContext(Dispatchers.IO) {
        try {
            val json = URL("https://api.github.com/repos/Mitsubishimas/fameli/releases/latest").readText()
            val tagStart = json.indexOf("\"tag_name\":\"") + 12
            val tagEnd = json.indexOf("\"", tagStart)
            json.substring(tagStart, tagEnd).removePrefix("v")
        } catch (e: Exception) { null }
    }

    fun logout() = viewModelScope.launch { authRepository.signOut() }
}

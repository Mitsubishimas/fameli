package com.fameli.budget.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.BuildConfig
import com.fameli.budget.UpdateChecker
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    val currentVersion: String = BuildConfig.VERSION_NAME

    fun checkForUpdates() {
        UpdateChecker.check(context, showDialog = true)
    }

    fun logout() = viewModelScope.launch { authRepository.signOut() }
}

package com.fameli.budget

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.fameli.budget.data.local.FameliDatabase
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import com.fameli.budget.data.remote.ApiClient
import com.fameli.budget.data.repository.FamilyManager
import com.fameli.budget.data.repository.FamilySyncRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FameliApp : Application() {
    
    @Inject lateinit var database: FameliDatabase
    @Inject lateinit var familySyncRepository: FamilySyncRepository
    @Inject lateinit var familyManager: FamilyManager

    override fun onCreate() {
        super.onCreate()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel("default", "Уведомления", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            addDefaultCategories()
            loadFamilyAndSync()
        }
    }
    
    private suspend fun addDefaultCategories() {
        val dao = database.categoryDao()
        val existing = dao.getAll().first()
        if (existing.isNotEmpty()) return
        
        val defaults = listOf(
            CategoryEntity(name = "Продукты", type = CategoryType.EXPENSE, icon = "🍔", isDefault = true),
            CategoryEntity(name = "Транспорт", type = CategoryType.EXPENSE, icon = "🚗", isDefault = true),
            CategoryEntity(name = "Жильё", type = CategoryType.EXPENSE, icon = "🏠", isDefault = true),
            CategoryEntity(name = "Развлечения", type = CategoryType.EXPENSE, icon = "🎮", isDefault = true),
            CategoryEntity(name = "Здоровье", type = CategoryType.EXPENSE, icon = "💊", isDefault = true),
            CategoryEntity(name = "Одежда", type = CategoryType.EXPENSE, icon = "👕", isDefault = true),
            CategoryEntity(name = "Зарплата", type = CategoryType.INCOME, icon = "💼", isDefault = true),
            CategoryEntity(name = "Подарки", type = CategoryType.INCOME, icon = "🎁", isDefault = true),
        )
        defaults.forEach { dao.insert(it) }
    }
    
    private suspend fun loadFamilyAndSync() {
        try {
            val families = ApiClient.getFamilies()
            if (families.length() > 0) {
                familyManager.currentFamilyId = families.getJSONObject(0).optString("id")
                familySyncRepository.syncAllFromCloud()
            }
        } catch (_: Exception) {}
    }
}

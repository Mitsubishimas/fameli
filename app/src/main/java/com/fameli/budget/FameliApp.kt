package com.fameli.budget

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.fameli.budget.data.local.FameliDatabase
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import com.fameli.budget.data.remote.ApiClient
import com.fameli.budget.data.remote.AppLogger
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
        
        AppLogger.log("APP", "Приложение запущено")
        
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
        try {
            val dao = database.categoryDao()
            val existing = dao.getAll().first()
            if (existing.isEmpty()) {
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
                AppLogger.log("APP", "Категории созданы: ${defaults.size}")
            } else {
                AppLogger.log("APP", "Категории уже есть: ${existing.size}")
            }
        } catch (e: Exception) {
            AppLogger.log("APP", "Ошибка категорий: ${e.message}")
        }
    }
    
    private suspend fun loadFamilyAndSync() {
        try {
            AppLogger.log("APP", "Загрузка семей...")
            val families = ApiClient.getFamilies()
            AppLogger.log("APP", "Семей получено: ${families.length()}")
            
            if (families.length() > 0) {
                val fid = families.getJSONObject(0).optString("id")
                familyManager.currentFamilyId = fid
                AppLogger.log("APP", "Семья: $fid")
                
                val result = familySyncRepository.syncAllFromCloud()
                result.fold(
                    onSuccess = { AppLogger.log("APP", "Синхронизация при старте: УСПЕХ") },
                    onFailure = { e -> AppLogger.log("APP", "Синхронизация при старте: ОШИБКА ${e.message}") }
                )
            } else {
                AppLogger.log("APP", "Семей нет")
            }
        } catch (e: Exception) {
            AppLogger.log("APP", "Ошибка загрузки: ${e.message}")
        }
    }
}

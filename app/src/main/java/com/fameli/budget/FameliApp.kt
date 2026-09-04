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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
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
            cleanBadCategories()
            addDefaultCategories()
            loadFamilyAndSync()
            startAutoSync()
        }
    }
    
    private suspend fun cleanBadCategories() {
        try {
            val dao = database.categoryDao()
            val all = dao.getAll().first()
            // Удаляем категории с пустым именем или ????? иконкой
            all.forEach { cat ->
                if (cat.name.isBlank() || cat.icon == "????" || cat.icon == "?" || cat.icon.isBlank()) {
                    dao.softDelete(cat.id)
                    AppLogger.log("APP", "Удалена плохая категория: ${cat.name}")
                }
            }
            // Удаляем дубликаты по имени
            val seen = mutableMapOf<String, Long>()
            dao.getAll().first().forEach { cat ->
                val key = "${cat.type}_${cat.name}"
                if (seen.containsKey(key)) {
                    dao.softDelete(cat.id)
                    AppLogger.log("APP", "Удалён дубликат: ${cat.name}")
                } else {
                    seen[key] = cat.id
                }
            }
        } catch (_: Exception) {}
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
            }
        } catch (_: Exception) {}
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

    private suspend fun startAutoSync() {
        while (true) {
            delay(5 * 60 * 1000)
            try {
                familySyncRepository.syncAllFromCloud()
            } catch (_: Exception) {}
        }
    }
}

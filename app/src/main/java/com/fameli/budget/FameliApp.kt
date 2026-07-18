package com.fameli.budget

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.fameli.budget.data.local.FameliDatabase
import com.fameli.budget.data.local.entity.CategoryEntity
import com.fameli.budget.data.local.entity.CategoryType
import com.fameli.budget.data.repository.FamilySyncRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FameliApp : Application() {
    
    @Inject lateinit var database: FameliDatabase
    @Inject lateinit var familySyncRepository: FamilySyncRepository

    override fun onCreate() {
        super.onCreate()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel("default", "Уведомления", NotificationManager.IMPORTANCE_DEFAULT)
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel("task_reminders", "Напоминания о задачах", NotificationManager.IMPORTANCE_HIGH)
            )
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            addDefaultCategories()
        }
    }
    
    private suspend fun addDefaultCategories() {
        val dao = database.categoryDao()
        
        val defaultCategories = listOf(
            CategoryEntity(name = "Продукты", type = CategoryType.EXPENSE, icon = "🍔", color = 0xFFE91E63, isDefault = true),
            CategoryEntity(name = "Транспорт", type = CategoryType.EXPENSE, icon = "🚗", color = 0xFF2196F3, isDefault = true),
            CategoryEntity(name = "Жильё", type = CategoryType.EXPENSE, icon = "🏠", color = 0xFF4CAF50, isDefault = true),
            CategoryEntity(name = "Развлечения", type = CategoryType.EXPENSE, icon = "🎮", color = 0xFFFF9800, isDefault = true),
            CategoryEntity(name = "Здоровье", type = CategoryType.EXPENSE, icon = "💊", color = 0xFF9C27B0, isDefault = true),
            CategoryEntity(name = "Образование", type = CategoryType.EXPENSE, icon = "📚", color = 0xFF795548, isDefault = true),
            CategoryEntity(name = "Одежда", type = CategoryType.EXPENSE, icon = "👕", color = 0xFF607D8B, isDefault = true),
            CategoryEntity(name = "Зарплата", type = CategoryType.INCOME, icon = "💼", color = 0xFF009688, isDefault = true),
            CategoryEntity(name = "Подарки", type = CategoryType.INCOME, icon = "🎁", color = 0xFFFF5722, isDefault = true),
            CategoryEntity(name = "Инвестиции", type = CategoryType.INCOME, icon = "📈", color = 0xFF3F51B5, isDefault = true),
        )
        
        defaultCategories.forEach { category ->
            dao.insert(category)
        }
    }
}

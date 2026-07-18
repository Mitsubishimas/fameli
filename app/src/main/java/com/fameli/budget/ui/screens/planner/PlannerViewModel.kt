package com.fameli.budget.ui.screens.planner

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.TaskDao
import com.fameli.budget.data.local.entity.TaskEntity
import com.fameli.budget.firebase.FirebaseAuthRepository
import com.fameli.budget.worker.TaskReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val application: Application,
    private val taskDao: TaskDao,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    val tasks: StateFlow<List<TaskEntity>> = _selectedDate.flatMapLatest { date ->
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)
        taskDao.getForDate(startOfDay, endOfDay)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newTaskTitle = MutableStateFlow("")
    val newTaskDesc = MutableStateFlow("")
    val newTaskTime = MutableStateFlow("")
    val newTaskDate = MutableStateFlow(System.currentTimeMillis())
    val showAddDialog = MutableStateFlow(false)

    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
    }

    fun showAdd() { 
        showAddDialog.value = true
        newTaskDate.value = _selectedDate.value
    }
    fun hideAdd() { showAddDialog.value = false; clearForm() }
    
    private fun clearForm() {
        newTaskTitle.value = ""
        newTaskDesc.value = ""
        newTaskTime.value = ""
    }

    fun addTask() = viewModelScope.launch {
        val title = newTaskTitle.value
        if (title.isBlank()) return@launch
        
        val task = TaskEntity(
            cloudId = UUID.randomUUID().toString(),
            title = title,
            description = newTaskDesc.value,
            date = newTaskDate.value,
            time = newTaskTime.value.ifBlank { "12:00" },
            createdBy = authRepository.currentUser.value?.email ?: "Я",
            createdByUid = authRepository.getUserId() ?: ""
        )
        val taskId = taskDao.insert(task)

        // Планируем уведомление
        TaskReminderScheduler.schedule(
            context = application,
            taskId = taskId,
            taskDate = newTaskDate.value,
            taskTime = newTaskTime.value.ifBlank { "12:00" },
            taskTitle = title,
            taskDesc = newTaskDesc.value
        )

        hideAdd()
    }

    fun toggleComplete(task: TaskEntity) = viewModelScope.launch {
        taskDao.toggleComplete(task.id, !task.isCompleted)
    }

    fun deleteTask(task: TaskEntity) = viewModelScope.launch {
        taskDao.softDelete(task.id)
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEndOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }
}

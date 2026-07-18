package com.fameli.budget.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.TaskDao
import com.fameli.budget.data.local.entity.TaskEntity
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    
    val tasks: StateFlow<List<TaskEntity>> = taskDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newTaskTitle = MutableStateFlow("")
    val newTaskDesc = MutableStateFlow("")
    val newTaskDate = MutableStateFlow(System.currentTimeMillis())
    val newTaskTime = MutableStateFlow("")
    val showAddDialog = MutableStateFlow(false)

    fun showAdd() { showAddDialog.value = true }
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
        taskDao.insert(task)
        hideAdd()
    }

    fun toggleComplete(task: TaskEntity) = viewModelScope.launch {
        taskDao.toggleComplete(task.id, !task.isCompleted)
    }

    fun deleteTask(task: TaskEntity) = viewModelScope.launch {
        taskDao.softDelete(task.id)
    }
}

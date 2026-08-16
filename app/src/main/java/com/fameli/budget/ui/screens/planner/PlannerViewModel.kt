package com.fameli.budget.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.TaskDao
import com.fameli.budget.data.local.entity.TaskEntity
import com.fameli.budget.data.repository.FamilySyncRepository
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val authRepository: FirebaseAuthRepository,
    private val familyRepo: FamilySyncRepository
) : ViewModel() {

    val selectedDate = MutableStateFlow(System.currentTimeMillis())
    val newTaskTime = MutableStateFlow("12:00")

    val tasks: StateFlow<List<TaskEntity>> = selectedDate.flatMapLatest { date ->
        val cal = Calendar.getInstance().apply { timeInMillis = date }
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        taskDao.getForDate(start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(date: Long) { selectedDate.value = date }

    fun addTask(title: String, description: String) = viewModelScope.launch {
        val task = TaskEntity(cloudId = UUID.randomUUID().toString(), title = title, description = description, date = selectedDate.value, time = newTaskTime.value, createdBy = authRepository.getUserName(), createdByUid = authRepository.getUserId() ?: "")
        taskDao.insert(task)
        val families = familyRepo.getMyFamilies()
        if (families.isNotEmpty()) familyRepo.syncTask(families.first(), task)
    }

    fun toggleComplete(task: TaskEntity) = viewModelScope.launch {
        taskDao.toggleComplete(task.id, !task.isCompleted)
        val families = familyRepo.getMyFamilies()
        if (families.isNotEmpty()) familyRepo.syncTask(families.first(), task.copy(isCompleted = !task.isCompleted))
    }

    fun deleteTask(task: TaskEntity) = viewModelScope.launch { taskDao.softDelete(task.id) }
}

package com.fameli.budget.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fameli.budget.data.local.dao.GoalDao
import com.fameli.budget.data.local.entity.GoalEntity
import com.fameli.budget.data.local.entity.GoalTransactionEntity
import com.fameli.budget.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    val goals = goalDao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val showAddDialog = MutableStateFlow(false)
    val showAddMoneyDialog = MutableStateFlow<Long?>(null)

    fun getTransactions(goalId: Long) = goalDao.getTransactions(goalId)

    fun showAdd() { showAddDialog.value = true }
    fun hideAdd() { showAddDialog.value = false }
    fun showAddMoney(goalId: Long) { showAddMoneyDialog.value = goalId }
    fun hideAddMoney() { showAddMoneyDialog.value = null }

    fun addGoal(title: String, desc: String, targetStr: String) = viewModelScope.launch {
        val target = targetStr.toDoubleOrNull() ?: return@launch
        goalDao.insertGoal(GoalEntity(cloudId = UUID.randomUUID().toString(), title = title, description = desc, targetAmount = target))
        hideAdd()
    }

    fun addMoney(goalId: Long, isAdding: Boolean, amountStr: String, comment: String) = viewModelScope.launch {
        var amount = amountStr.toDoubleOrNull() ?: return@launch
        if (!isAdding) amount = -amount
        goalDao.insertTransaction(GoalTransactionEntity(goalId = goalId, amount = amount, comment = comment.ifBlank { "Без комментария" }, userName = authRepository.currentUser.value?.email ?: "Я", userUid = authRepository.getUserId() ?: ""))
        val goal = goalDao.getById(goalId) ?: return@launch
        goalDao.updateAmount(goalId, goal.currentAmount + amount)
        hideAddMoney()
    }

    fun deleteGoal(goal: GoalEntity) = viewModelScope.launch { goalDao.softDelete(goal.id) }
    fun toggleComplete(goal: GoalEntity) = viewModelScope.launch { goalDao.toggleComplete(goal.id, !goal.isCompleted) }
}

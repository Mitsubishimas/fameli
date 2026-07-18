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

    val goals: StateFlow<List<GoalEntity>> = goalDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val showAddDialog = MutableStateFlow(false)
    val showAddMoneyDialog = MutableStateFlow<Long?>(null) // goalId
    val newGoalTitle = MutableStateFlow("")
    val newGoalDesc = MutableStateFlow("")
    val newGoalTarget = MutableStateFlow("")
    val addAmount = MutableStateFlow("")
    val addComment = MutableStateFlow("")

    fun showAdd() { showAddDialog.value = true; clearGoalForm() }
    fun hideAdd() { showAddDialog.value = false }
    fun showAddMoney(goalId: Long) { showAddMoneyDialog.value = goalId; clearMoneyForm() }
    fun hideAddMoney() { showAddMoneyDialog.value = null }

    private fun clearGoalForm() { newGoalTitle.value = ""; newGoalDesc.value = ""; newGoalTarget.value = "" }
    private fun clearMoneyForm() { addAmount.value = ""; addComment.value = "" }

    fun addGoal() = viewModelScope.launch {
        val target = newGoalTarget.value.toDoubleOrNull() ?: return@launch
        val goal = GoalEntity(
            cloudId = UUID.randomUUID().toString(),
            title = newGoalTitle.value,
            description = newGoalDesc.value,
            targetAmount = target
        )
        goalDao.insertGoal(goal)
        hideAdd()
    }

    fun addMoney(goalId: Long, isAdding: Boolean) = viewModelScope.launch {
        val amountStr = addAmount.value
        if (amountStr.isBlank()) return@launch
        var amount = amountStr.toDoubleOrNull() ?: return@launch
        if (!isAdding) amount = -amount // отрицательное = снятие

        val comment = addComment.value.ifBlank { if (isAdding) "Пополнение" else "Снятие" }
        val userName = authRepository.currentUser.value?.email ?: "Пользователь"

        val transaction = GoalTransactionEntity(
            cloudId = UUID.randomUUID().toString(),
            goalId = goalId,
            amount = amount,
            comment = comment,
            userName = userName,
            userUid = authRepository.getUserId() ?: ""
        )
        goalDao.insertTransaction(transaction)

        // Обновляем сумму цели
        val goal = goalDao.getById(goalId) ?: return@launch
        val newTotal = goal.currentAmount + amount
        goalDao.updateAmount(goalId, newTotal)

        hideAddMoney()
    }

    fun deleteGoal(goal: GoalEntity) = viewModelScope.launch {
        goalDao.softDelete(goal.id)
    }

    fun toggleComplete(goal: GoalEntity) = viewModelScope.launch {
        goalDao.toggleComplete(goal.id, !goal.isCompleted)
    }
}

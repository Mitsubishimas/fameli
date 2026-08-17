package com.fameli.budget.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fameli.budget.data.local.entity.GoalEntity
import com.fameli.budget.ui.screens.family.FamilyViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.NumberFormat
import java.util.*

@Composable
fun GoalScreen(viewModel: GoalViewModel, showAddDialog: Boolean, onDismiss: () -> Unit) {
    val goals by viewModel.goals.collectAsState()
    val familyVM: FamilyViewModel = hiltViewModel()
    val isSyncing by familyVM.isSyncing.collectAsState()
    var goalTitle by remember { mutableStateOf("") }
    var goalDesc by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("") }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isSyncing),
        onRefresh = { familyVM.forceSync() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Цели", style = MaterialTheme.typography.headlineSmall) }
            if (goals.isEmpty()) {
                item { Card(Modifier.fillMaxWidth()) { Text("Нет целей", modifier = Modifier.padding(24.dp)) } }
            }
            items(goals) { goal ->
                GoalCard(goal, viewModel)
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Новая цель") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(goalTitle, { goalTitle = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(goalDesc, { goalDesc = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(goalTarget, { goalTarget = it }, label = { Text("Сумма") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { Button(onClick = { viewModel.addGoal(goalTitle, goalDesc, goalTarget); goalTitle = ""; goalDesc = ""; goalTarget = ""; onDismiss() }, enabled = goalTitle.isNotBlank()) { Text("Создать") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
        )
    }
}

@Composable
fun GoalCard(goal: GoalEntity, viewModel: GoalViewModel) {
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(goal.title, style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text("${(progress * 100).toInt()}% — ${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(goal.currentAmount)} / ${NumberFormat.getCurrencyInstance(Locale("ru","RU")).format(goal.targetAmount)}")
            Spacer(Modifier.height(8.dp))
            Button(onClick = { viewModel.showAddMoney(goal.id) }, modifier = Modifier.fillMaxWidth()) { Text("Пополнить") }
        }
    }
}

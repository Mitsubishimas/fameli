package com.fameli.budget.ui.screens.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fameli.budget.ui.screens.family.FamilyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, viewModel: AddTransactionViewModel = hiltViewModel()) {
    val amount by viewModel.amount.collectAsState()
    val note by viewModel.note.collectAsState()
    val isExpense by viewModel.isExpense.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selected by viewModel.selectedCategory.collectAsState()
    val familyVM: FamilyViewModel = hiltViewModel()
    val familyId by familyVM.familyId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.toggleType(true) }, modifier = Modifier.weight(1f)) { Text("Расход") }
                Button(onClick = { viewModel.toggleType(false) }, modifier = Modifier.weight(1f)) { Text("Доход") }
            }
            OutlinedTextField(amount, { viewModel.updateAmount(it) }, label = { Text("Сумма") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            
            Text("Категория")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { c ->
                    SuggestionChip(onClick = { viewModel.selectCategory(c) }, label = { Text("${c.icon} ${c.name}") })
                }
            }
            
            OutlinedTextField(note, { viewModel.updateNote(it) }, label = { Text("Заметка") }, modifier = Modifier.fillMaxWidth())
            
            Button(
                onClick = { viewModel.save(familyId); navController.navigateUp() },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotBlank() && selected != null
            ) { Text("Сохранить") }
        }
    }
}

package com.fameli.budget.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.fameli.budget.ui.screens.auth.*
import com.fameli.budget.ui.screens.categories.*
import com.fameli.budget.ui.screens.dashboard.*
import com.fameli.budget.ui.screens.family.*
import com.fameli.budget.ui.screens.planner.*
import com.fameli.budget.ui.screens.settings.*
import com.fameli.budget.ui.screens.statistics.*
import com.fameli.budget.ui.screens.transaction.*

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Dashboard : Screen("dashboard")
    object Statistics : Screen("statistics")
    object Planner : Screen("planner")
    object AddTransaction : Screen("add_transaction")
    object Categories : Screen("categories")
    object Family : Screen("family")
    object Settings : Screen("settings")
}

@Composable
fun FameliNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(navController, startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Auth.route) {
        composable(Screen.Auth.route) { AuthScreen(onSuccess = { navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Auth.route) { inclusive = true } } }) }
        composable(Screen.Dashboard.route) { MainScaffold(navController) }
        composable(Screen.Statistics.route) { MainScaffold(navController) }
        composable(Screen.Planner.route) { MainScaffold(navController) }
        composable(Screen.AddTransaction.route) { MainScaffold(navController) }
        composable(Screen.Categories.route) { MainScaffold(navController) }
        composable(Screen.Family.route) { MainScaffold(navController) }
        composable(Screen.Settings.route) { MainScaffold(navController) }
    }
}

@Composable
fun MainScaffold(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var showAddTaskDialog by remember { mutableStateOf(false) }
    val plannerViewModel: PlannerViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Triple(Screen.Dashboard, "Главная", Icons.Filled.Home),
                    Triple(Screen.Planner, "Планы", Icons.Filled.CalendarMonth),
                    Triple(Screen.Categories, "Категории", Icons.Filled.Category),
                    Triple(Screen.Family, "Семья", Icons.Filled.People),
                    Triple(Screen.Settings, "Ещё", Icons.Filled.MoreHoriz),
                ).forEach { (screen, title, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, title) },
                        label = { Text(title, style = MaterialTheme.typography.labelSmall) },
                        selected = currentRoute == screen.route,
                        onClick = { navController.navigate(screen.route) { popUpTo(Screen.Dashboard.route) { saveState = true }; launchSingleTop = true; restoreState = true } }
                    )
                }
            }
        },
        floatingActionButton = {
            when (currentRoute) {
                Screen.Planner.route -> {
                    FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                        Icon(Icons.Filled.Add, "Добавить задачу")
                    }
                }
                else -> {
                    FloatingActionButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) {
                        Icon(Icons.Filled.Add, "Добавить")
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (currentRoute) {
                Screen.Dashboard.route -> DashboardScreen()
                Screen.Statistics.route -> StatisticsScreen()
                Screen.Planner.route -> PlannerScreen(
                    viewModel = plannerViewModel,
                    showAddDialog = showAddTaskDialog,
                    onDismissDialog = { showAddTaskDialog = false }
                )
                Screen.AddTransaction.route -> AddTransactionScreen(navController)
                Screen.Categories.route -> CategoriesScreen()
                Screen.Family.route -> FamilyScreen()
                Screen.Settings.route -> SettingsScreen()
            }
        }
    }
}

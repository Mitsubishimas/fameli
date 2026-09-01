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
import com.fameli.budget.ui.screens.dashboard.*
import com.fameli.budget.ui.screens.goals.*
import com.fameli.budget.ui.screens.planner.*
import com.fameli.budget.ui.screens.settings.*
import com.fameli.budget.ui.screens.shopping.*
import com.fameli.budget.ui.screens.statistics.*
import com.fameli.budget.ui.screens.transaction.*

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Dashboard : Screen("dashboard")
    object Statistics : Screen("statistics")
    object Goals : Screen("goals")
    object Shopping : Screen("shopping")
    object Planner : Screen("planner")
    object AddTransaction : Screen("add_transaction")
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
        composable(Screen.Goals.route) { MainScaffold(navController) }
        composable(Screen.Shopping.route) { MainScaffold(navController) }
        composable(Screen.Planner.route) { MainScaffold(navController) }
        composable(Screen.AddTransaction.route) { MainScaffold(navController) }
        composable(Screen.Settings.route) { MainScaffold(navController) }
    }
}

@Composable
fun MainScaffold(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val goalVM: GoalViewModel = hiltViewModel()
    val plannerVM: PlannerViewModel = hiltViewModel()
    val shoppingVM: ShoppingViewModel = hiltViewModel()
    var showGoalDialog by remember { mutableStateOf(false) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var showShoppingDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = currentRoute == Screen.Dashboard.route, onClick = { navController.navigate(Screen.Dashboard.route) { popUpTo(0); launchSingleTop = true } }, icon = { Icon(Icons.Filled.Home, "Главная") }, label = { Text("Главная") })
                NavigationBarItem(selected = currentRoute == Screen.Statistics.route, onClick = { navController.navigate(Screen.Statistics.route) { popUpTo(0); launchSingleTop = true } }, icon = { Icon(Icons.Filled.PieChart, "Аналитика") }, label = { Text("Аналитика") })
                NavigationBarItem(selected = currentRoute == Screen.Goals.route, onClick = { navController.navigate(Screen.Goals.route) { popUpTo(0); launchSingleTop = true } }, icon = { Icon(Icons.Filled.Flag, "Цели") }, label = { Text("Цели") })
                NavigationBarItem(selected = currentRoute == Screen.Shopping.route, onClick = { navController.navigate(Screen.Shopping.route) { popUpTo(0); launchSingleTop = true } }, icon = { Icon(Icons.Filled.ShoppingCart, "Покупки") }, label = { Text("Покупки") })
                NavigationBarItem(selected = currentRoute == Screen.Planner.route, onClick = { navController.navigate(Screen.Planner.route) { popUpTo(0); launchSingleTop = true } }, icon = { Icon(Icons.Filled.CalendarMonth, "Планы") }, label = { Text("Планы") })
                NavigationBarItem(selected = currentRoute == Screen.Settings.route, onClick = { navController.navigate(Screen.Settings.route) { popUpTo(0); launchSingleTop = true } }, icon = { Icon(Icons.Filled.Settings, "Ещё") }, label = { Text("Ещё") })
            }
        },
        floatingActionButton = {
            when (currentRoute) {
                Screen.Dashboard.route -> FloatingActionButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) { Icon(Icons.Filled.Add, "Добавить") }
                Screen.Goals.route -> FloatingActionButton(onClick = { showGoalDialog = true }) { Icon(Icons.Filled.Add, "Цель") }
                Screen.Shopping.route -> FloatingActionButton(onClick = { showShoppingDialog = true }) { Icon(Icons.Filled.Add, "Покупка") }
                Screen.Planner.route -> FloatingActionButton(onClick = { showTaskDialog = true }) { Icon(Icons.Filled.Add, "Задача") }
                else -> {}
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (currentRoute) {
                Screen.Dashboard.route -> DashboardScreen()
                Screen.Statistics.route -> StatisticsScreen()
                Screen.Goals.route -> GoalScreen(goalVM, showGoalDialog) { showGoalDialog = false }
                Screen.Shopping.route -> ShoppingScreen(shoppingVM, showShoppingDialog) { showShoppingDialog = false }
                Screen.Planner.route -> PlannerScreen(plannerVM, showTaskDialog) { showTaskDialog = false }
                Screen.AddTransaction.route -> AddTransactionScreen(navController)
                Screen.Settings.route -> SettingsScreen()
            }
        }
    }
}

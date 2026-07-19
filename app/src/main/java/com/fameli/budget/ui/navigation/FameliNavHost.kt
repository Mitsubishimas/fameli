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
import com.fameli.budget.ui.screens.goals.*
import com.fameli.budget.ui.screens.planner.*
import com.fameli.budget.ui.screens.settings.*
import com.fameli.budget.ui.screens.statistics.*
import com.fameli.budget.ui.screens.transaction.*

sealed class Screen(val route: String, val title: String) {
    object Auth : Screen("auth", "Вход")
    object Dashboard : Screen("dashboard", "Главная")
    object Statistics : Screen("statistics", "Статистика")
    object Planner : Screen("planner", "Планы")
    object Goals : Screen("goals", "Цели")
    object AddTransaction : Screen("add_transaction", "Добавить")
    object Categories : Screen("categories", "Категории")
    object Family : Screen("family", "Семья")
    object Settings : Screen("settings", "Ещё")
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
        composable(Screen.Goals.route) { MainScaffold(navController) }
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

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Screen.Dashboard.route,
                    onClick = { navController.navigate(Screen.Dashboard.route) { popUpTo(0); launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.Home, "Главная") },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Goals.route,
                    onClick = { navController.navigate(Screen.Goals.route) { popUpTo(0); launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.Flag, "Цели") },
                    label = { Text("Цели") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Planner.route,
                    onClick = { navController.navigate(Screen.Planner.route) { popUpTo(0); launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.CalendarMonth, "Планы") },
                    label = { Text("Планы") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Family.route,
                    onClick = { navController.navigate(Screen.Family.route) { popUpTo(0); launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.People, "Семья") },
                    label = { Text("Семья") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Settings.route,
                    onClick = { navController.navigate(Screen.Settings.route) { popUpTo(0); launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.Settings, "Ещё") },
                    label = { Text("Ещё") }
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (currentRoute) {
                Screen.Dashboard.route -> DashboardScreen()
                Screen.Statistics.route -> StatisticsScreen()
                Screen.Planner.route -> PlannerScreen(hiltViewModel(), false) {}
                Screen.Goals.route -> GoalScreen()
                Screen.AddTransaction.route -> AddTransactionScreen(navController)
                Screen.Categories.route -> CategoriesScreen()
                Screen.Family.route -> FamilyScreen()
                Screen.Settings.route -> SettingsScreen()
            }
        }
    }
}

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
                listOf(
                    Quint(Screen.Dashboard, "Главная", Icons.Filled.Home, Icons.Filled.Home),
                    Quint(Screen.Statistics, "Статистика", Icons.Filled.PieChart, Icons.Filled.PieChart),
                    Quint(Screen.Planner, "Планы", Icons.Filled.CalendarMonth, Icons.Filled.CalendarMonth),
                    Quint(Screen.Categories, "Категории", Icons.Filled.Category, Icons.Filled.Category),
                    Quint(Screen.Settings, "Ещё", Icons.Filled.MoreHoriz, Icons.Filled.MoreHoriz),
                ).forEach { (screen, title, icon, _) ->
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
            if (currentRoute != Screen.AddTransaction.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) { Icon(Icons.Filled.Add, "Добавить") }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (currentRoute) {
                Screen.Dashboard.route -> DashboardScreen()
                Screen.Statistics.route -> StatisticsScreen()
                Screen.Planner.route -> PlannerScreen()
                Screen.AddTransaction.route -> AddTransactionScreen(navController)
                Screen.Categories.route -> CategoriesScreen()
                Screen.Settings.route -> SettingsScreen()
            }
        }
    }
}

data class Quint<T1, T2, T3, T4>(val first: T1, val second: T2, val third: T3, val fourth: T4)

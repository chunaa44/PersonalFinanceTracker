package com.example.personalfinancetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.personalfinancetracker.ui.entry.EntryScreen
import com.example.personalfinancetracker.ui.entry.EntryViewModel
import com.example.personalfinancetracker.ui.home.HomeScreen
import com.example.personalfinancetracker.ui.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val ENTRY = "entry?transactionId={transactionId}"

    // id = 0 means "add new"; any other id means "edit that record".
    fun entry(id: Int = 0) = "entry?transactionId=$id"
}

@Composable
fun FinanceNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onAdd = { navController.navigate(Routes.entry()) },
                onEdit = { id -> navController.navigate(Routes.entry(id)) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(
            route = Routes.ENTRY,
            arguments = listOf(navArgument(EntryViewModel.TRANSACTION_ID_ARG) {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            EntryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
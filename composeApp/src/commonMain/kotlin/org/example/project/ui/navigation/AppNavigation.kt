package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.example.project.persistence.database.DriverFactory
import org.example.project.persistence.database.createDatabase
import org.example.project.ui.auth.AuthScreen
import org.example.project.ui.main.MainScreen
import org.example.project.ui.main.model.ModelDetailsScreen
import org.example.project.ui.main.model.ModelNewScreen
import org.example.project.viewModel.ItemViewModel
import org.example.project.viewModel.PersonViewModel
import org.example.project.viewModel.SaleViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Initialize database
    val database = runBlocking { createDatabase(DriverFactory()) }

    val personViewModel = PersonViewModel(database)
    val itemViewModel = ItemViewModel(database)
    val saleViewModel = SaleViewModel(database)

    NavHost(navController = navController, startDestination = "log_reg_screen") {
        composable("log_reg_screen") {
            AuthScreen(navController = navController, personViewModel = personViewModel)
        }
        composable("main_app_view") {
            MainScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                saleViewModel = saleViewModel
            )
        }
        // Detailed view of a model
        composable("model_details_screen") {
            ModelDetailsScreen(navController = navController, itemViewModel = itemViewModel, previousRoute = "main_app_view")
        }
        // Screen to add new model to the database
        composable("model_add_new"){
            ModelNewScreen(navController = navController, itemViewModel = itemViewModel, previousRoute = "main_app_view")
        }
    }
}


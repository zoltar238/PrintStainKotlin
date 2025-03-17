package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.example.project.PrintStainDatabase
import org.example.project.persistence.database.DriverFactory
import org.example.project.persistence.database.createDatabase
import org.example.project.ui.auth.AuthScreen
import org.example.project.ui.main.MainScreen
import org.example.project.ui.main.ModelDetailsScreen
import org.example.project.viewModel.ItemViewModel
import org.example.project.viewModel.PersonViewModel
import org.example.project.viewModel.SaleViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val database: PrintStainDatabase
    lateinit var itemViewModel: ItemViewModel
    lateinit var saleViewModel: SaleViewModel
    // Initialize database
    runBlocking {
        database = createDatabase(DriverFactory())
    }

    val personViewModel = PersonViewModel(database = database)
    val personUiState by personViewModel.personUiState.collectAsState()
    if (personUiState.response == "User has logged in correctly") {
        println("loggeado con exito")

    }

    NavHost(navController = navController, startDestination = "main_app_view") {
        composable("log_reg_screen") {
            AuthScreen(navController = navController, personViewModel = personViewModel)
        }
        composable("main_app_view") {
            runBlocking {
                itemViewModel = ItemViewModel(database = database)
                saleViewModel = SaleViewModel(database = database)
            }
            MainScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                saleViewModel = saleViewModel
            )
        }
        composable("model_details_screen") {
            ModelDetailsScreen(navController = navController, itemViewModel = itemViewModel)
        }
    }
}


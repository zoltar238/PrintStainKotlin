package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.example.project.PrintStainDatabase
import org.example.project.persistence.database.DriverFactory
import org.example.project.persistence.database.createDatabase
import org.example.project.service.ItemViewModel
import org.example.project.service.SaleViewModel
import org.example.project.service.autoLogin
import org.example.project.ui.auth.AuthScreen
import org.example.project.ui.main.MainScreen
import org.example.project.ui.main.ModelDetailsScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val database: PrintStainDatabase

    // Check if login was possible with saved credentials
    var loggedWithPreferences: Boolean

    // Initialize necessary variables
    runBlocking {
        loggedWithPreferences = autoLogin()
        database = createDatabase(DriverFactory())
    }

    // Ahora que la base de datos está inicializada, inicializa los ViewModels
    val sharedItemViewModel = ItemViewModel(database = database)
    val sharedSaleViewModel = SaleViewModel(database = database)

    val startDestination = if (loggedWithPreferences) "main_app_view" else "log_reg_screen"

    NavHost(navController = navController, startDestination = startDestination) {
        // El resto de tu código permanece igual
        composable("log_reg_screen") {
            AuthScreen(navController = navController)
        }
        composable("main_app_view") {
            MainScreen(
                navController = navController,
                itemViewModel = sharedItemViewModel,
                saleViewModel = sharedSaleViewModel
            )
        }
        composable("model_details_screen") {
            ModelDetailsScreen(navController = navController, itemViewModel = sharedItemViewModel)
        }
    }
}


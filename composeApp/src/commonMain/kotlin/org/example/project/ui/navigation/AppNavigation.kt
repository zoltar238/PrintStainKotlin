package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

// Objeto de módulo para inyección de dependencias
object AppModule {
    private val driverFactory by lazy { DriverFactory() }

    val database by lazy {
        runBlocking { createDatabase(driverFactory) }
    }

    val personViewModel by lazy { PersonViewModel(database) }
    val itemViewModel by lazy { ItemViewModel(database) }
    val saleViewModel by lazy { SaleViewModel(database) }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "log_reg_screen") {
        composable("log_reg_screen") {
            AuthScreen(
                navController = navController,
                personViewModel = AppModule.personViewModel
            )
        }
        composable("main_app_view") {
            MainScreen(
                navController = navController,
                itemViewModel = AppModule.itemViewModel,
                saleViewModel = AppModule.saleViewModel
            )
        }
        // Detailed view of a model
        composable("model_details_screen") {
            ModelDetailsScreen(
                navController = navController,
                itemViewModel = AppModule.itemViewModel,
                previousRoute = "main_app_view",
                saleViewModel = AppModule.saleViewModel
            )
        }
        // Screen to add new model to the database
        composable(route = "model_add_new?option={option}", arguments = listOf(
            navArgument("option") {
                type = NavType.StringType
                defaultValue = "new"
                nullable = false
            }
        )) { backStackEntry ->
            val option = backStackEntry.arguments!!.getString("option")
            ModelNewScreen(
                navController = navController,
                itemViewModel = AppModule.itemViewModel,
                previousRoute = "main_app_view",
                option = option!!
            )
        }
    }
}


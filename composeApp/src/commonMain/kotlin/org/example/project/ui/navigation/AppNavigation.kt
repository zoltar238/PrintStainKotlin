package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.example.project.service.autoLogin
import org.example.project.ui.auth.AuthScreen
import org.example.project.ui.main.MainScreen
import org.example.project.ui.main.ModelDetailsScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Check if login was possible with saved credentials
    var loggedWithPreferences: Boolean
    runBlocking {
        loggedWithPreferences = autoLogin()
    }

    // todo: preference check is not working properly

    val startDestination = if (loggedWithPreferences) "main_app_view" else "log_reg_screen"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("log_reg_screen") {
            AuthScreen(navController = navController)
        }
        composable("main_app_view") {
            MainScreen(navController = navController) // La vista principal después del login
        }
        composable("model_details_screen/{itemId}") {
            // Transfer item object
            val itemId = it.arguments?.getString("itemId")
            if (itemId != null) {
                ModelDetailsScreen(navController = navController, itemId)
            }
        }
    }
}

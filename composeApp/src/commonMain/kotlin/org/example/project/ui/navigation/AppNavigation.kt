package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.controller.loginController
import org.example.project.ui.auth.AuthScreen
import org.example.project.ui.main.MainScreen
import org.example.project.ui.main.ModelDetailsScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var loggedWithPreferences by remember { mutableStateOf<Boolean?>(null) } // Estado para manejar la lógica de login


    // Check if login was possible with saved credential
    LaunchedEffect(Unit) {
        loggedWithPreferences = loginController()
    }

    // todo: preference check is not working properly
    println(loggedWithPreferences)

    val startDestination = if (loggedWithPreferences == true) "log_reg_screen" else "main_app_view"

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

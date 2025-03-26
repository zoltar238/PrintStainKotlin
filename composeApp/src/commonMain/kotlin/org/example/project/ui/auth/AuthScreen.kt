package org.example.project.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.MessageToaster
import org.example.project.viewModel.PersonViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun AuthScreen(navController: NavHostController, personViewModel: PersonViewModel) {
    // Save screen that needs to be shown
    var isRegisterMode by remember { mutableStateOf(true) }
    // Person view model:
    val personUiState by personViewModel.personUiState.collectAsState()

    // Navigation
    LaunchedEffect(personUiState.messageEvent?.message) {
        if (personUiState.messageEvent?.message == "User has logged in correctly") {
            navController.navigate(route = "main_app_view")
        }
    }

    MaterialTheme {
        // Loading indicator
        if (personUiState.isLoading) LoadingIndicator()
        // Toast
        MessageToaster(
            messageEvent = personUiState.messageEvent,
            success = personUiState.success,
            onMessageConsumed = { personViewModel.consumeMessage() }
        )
        Column(
            Modifier.fillMaxHeight().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Row para botones de alternancia (Registro | Iniciar sesión)
            Row(
                modifier = Modifier.fillMaxWidth().zIndex(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { isRegisterMode = true },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isRegisterMode) MaterialTheme.colors.primary else Color.Gray
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Registro")
                }

                Button(
                    onClick = { isRegisterMode = false },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (!isRegisterMode) MaterialTheme.colors.primary else Color.Gray
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Iniciar sesión")
                }
            }

            // Formulario de Registro
            if (isRegisterMode) {
                RegisterScreen(personViewModel = personViewModel)
            } else {
                LoginScreen(personViewModel = personViewModel)
            }
        }
    }
}
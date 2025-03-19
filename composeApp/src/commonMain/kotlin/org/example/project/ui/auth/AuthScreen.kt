package org.example.project.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.ui.AppColors
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.SnackBarComponent
import org.example.project.viewModel.PersonViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun AuthScreen(navController: NavHostController, personViewModel: PersonViewModel) {
    // SnackBar
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarScope = rememberCoroutineScope()
    // Snack-bar color
    val snackBarColor = remember { mutableStateOf(AppColors.primaryColor) }
    // Save screen that needs to be shown
    var isRegisterMode by remember { mutableStateOf(true) }
    // Person view model:
    val personUiState by personViewModel.personUiState.collectAsState()

    // Change snackbar color based on state
    LaunchedEffect(personUiState.success) {
        snackBarColor.value = if (personUiState.success) AppColors.primaryColor else AppColors.errorColor
    }

    // Show snackbar when response changes
    LaunchedEffect(personUiState.response) {
        if (!personUiState.isLoading && personUiState.response != null) {
            snackbarHostState.showSnackbar(
                message = personUiState.response!!,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Navigation
    LaunchedEffect(personUiState.response) {
        if (!personUiState.isLoading && personUiState.response == "User has logged in correctly") {
            navController.navigate(route = "main_app_view")
        }
    }

    MaterialTheme {
        // Loading indicator
        if (personUiState.isLoading) LoadingIndicator()
        // Snackbar
        SnackBarComponent(
            snackbarHostState = snackbarHostState,
            snackBarColor = snackBarColor
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
                RegisterScreen(
                    onShowSnackBar = onShowSnackbar(snackBarColor, snackBarScope, snackbarHostState),
                    personViewModel = personViewModel
                )
            } else {
                LoginScreen(
                    onShowSnackBar = onShowSnackbar(snackBarColor, snackBarScope, snackbarHostState),
                    personViewModel = personViewModel
                )
            }
        }
    }
}

private fun onShowSnackbar(
    snackBarColor: MutableState<Color>,
    snackBarScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
): (String, Boolean) -> Unit = { message, isError ->
    snackBarColor.value = if (isError) AppColors.errorColor else AppColors.primaryColor
    snackBarScope.launch {
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }
}

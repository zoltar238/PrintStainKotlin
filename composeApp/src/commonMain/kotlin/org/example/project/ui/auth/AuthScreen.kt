package org.example.project.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.example.project.ui.AppColors
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun AuthScreen(navController: NavHostController) {
    // SnackBar
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarScope = rememberCoroutineScope()
    // Snack-bar color
    val snackBarColor = remember { mutableStateOf(AppColors.primaryColor) }
    // Save screen that needs to be shown
    var isRegisterMode by remember { mutableStateOf(true) }

    MaterialTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        backgroundColor = snackBarColor.value,
                        contentColor = MaterialTheme.colors.onPrimary,
                        elevation = 5.dp,
                    )
                }
            }
        ) {
            Column(
                Modifier.fillMaxHeight().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Row para botones de alternancia (Registro | Iniciar sesión)
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        snackBarScope,
                        snackbarHostState,
                        snackBarColor
                    )
                } else {
                    LoginScreen(
                        snackBarScope,
                        snackbarHostState,
                        snackBarColor,
                        navController
                    )
                }
            }
        }
    }
}
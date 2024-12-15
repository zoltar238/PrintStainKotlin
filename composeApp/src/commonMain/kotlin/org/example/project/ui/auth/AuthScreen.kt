package org.example.project.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun AuthScreen(
    navController: NavHostController
) {
    MaterialTheme {
        // SnackBar
        val snackbarHostState = remember { SnackbarHostState() }
        val snackBarScope = rememberCoroutineScope()
        // Initial snack-bar color
        val color = MaterialTheme.colors.primary
        val snackBarColor = remember { mutableStateOf(color) }

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
            // Estado para determinar qué formulario mostrar
            var isRegisterMode by remember { mutableStateOf(true) }

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
                    RegisterScreen(snackBarScope, snackbarHostState)
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
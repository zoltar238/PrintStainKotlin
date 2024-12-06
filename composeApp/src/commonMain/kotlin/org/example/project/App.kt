package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.commons.validator.routines.EmailValidator
import org.example.project.controller.IdentificationRequestController
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import printstain.composeapp.generated.resources.Res
import printstain.composeapp.generated.resources.email_field
import printstain.composeapp.generated.resources.name_field
import printstain.composeapp.generated.resources.password_field
import printstain.composeapp.generated.resources.repeat_password_field
import printstain.composeapp.generated.resources.surname_field
import printstain.composeapp.generated.resources.username_field

@Composable
@Preview
fun App() {
    MaterialTheme {
        // SnackBar
        val snackbarHostState = remember { SnackbarHostState() }
        val snackBarScope = rememberCoroutineScope()

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        backgroundColor = MaterialTheme.colors.primary,
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
                    RegisterForm(snackBarScope, snackbarHostState)
                } else {
                    LoginForm(snackBarScope, snackbarHostState)
                }

            }
        }
    }
}

@Composable
fun RegisterForm(snackBarScope: CoroutineScope, snackbarHostState: SnackbarHostState) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatedPassword by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var passwordCoincide by remember { mutableStateOf(true) }

    val commonModifier = Modifier.fillMaxWidth(1f).padding(horizontal = 40.dp).padding(vertical = 10.dp)

    OutlinedTextField(
        value = name,
        onValueChange = { newText -> name = newText },
        label = { Text(stringResource(Res.string.name_field)) },
        placeholder = { Text("Ejemplo: Hola Mundo") },
        singleLine = true,
        modifier = commonModifier
    )

    OutlinedTextField(
        value = surname,
        onValueChange = { newText -> surname = newText },
        label = { Text(stringResource(Res.string.surname_field)) },
        placeholder = { Text("Ejemplo: Hola Mundo") },
        singleLine = true,
        modifier = commonModifier
    )

    OutlinedTextField(
        value = username,
        onValueChange = { newText -> username = newText },
        label = { Text(stringResource(Res.string.username_field)) },
        placeholder = { Text("Ejemplo: Hola Mundo") },
        singleLine = true,
        modifier = commonModifier
    )

    OutlinedTextField(
        value = email,
        onValueChange = { newText ->
            email = newText
            isEmailValid = EmailValidator.getInstance().isValid(newText)
        },
        label = { Text(stringResource(Res.string.email_field)) },
        placeholder = { Text("Ejemplo: Hola Mundo") },
        singleLine = true,
        modifier = commonModifier,
        isError = !isEmailValid
    )

    OutlinedTextField(
        value = password,
        onValueChange = { newText -> password = newText },
        label = { Text(stringResource(Res.string.password_field)) },
        placeholder = { Text("*************") },
        singleLine = true,
        modifier = commonModifier,
        visualTransformation = PasswordVisualTransformation()
    )

    OutlinedTextField(
        value = repeatedPassword,
        onValueChange = { newText ->
            repeatedPassword = newText
            passwordCoincide = (password == repeatedPassword && repeatedPassword.isNotEmpty())
        },
        label = { Text(stringResource(Res.string.repeat_password_field)) },
        placeholder = { Text("*************") },
        singleLine = true,
        modifier = commonModifier,
        isError = !passwordCoincide,
        visualTransformation = PasswordVisualTransformation()
    )

    Button(
        onClick = {
            if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
                snackBarScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Please, fill all the fields",
                        duration = SnackbarDuration.Short
                    )
                }
            } else if (!passwordCoincide) {
                snackBarScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Passwords do not coincide",
                        duration = SnackbarDuration.Short
                    )
                }
            } else if (!isEmailValid) {
                snackBarScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Email is not valid",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    ) {
        Text("Register")
    }
}

@Composable
fun LoginForm(snackBarScope: CoroutineScope, snackbarHostState: SnackbarHostState) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val commonModifier = Modifier.fillMaxWidth(1f).padding(horizontal = 40.dp).padding(vertical = 10.dp)

    OutlinedTextField(
        value = username,
        onValueChange = { newText -> username = newText },
        label = { Text(stringResource(Res.string.username_field)) },
        placeholder = { Text("Ejemplo: Hola Mundo") },
        singleLine = true,
        modifier = commonModifier
    )

    OutlinedTextField(
        value = password,
        onValueChange = { newText -> password = newText },
        label = { Text(stringResource(Res.string.password_field)) },
        placeholder = { Text("*************") },
        singleLine = true,
        modifier = commonModifier,
        visualTransformation = PasswordVisualTransformation()
    )

    Button(
        onClick = {
            if (username.isEmpty() || password.isEmpty()) {
                snackBarScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Please, fill all the fields",
                        duration = SnackbarDuration.Short
                    )
                }
            } else {
                snackBarScope.launch {
                    try {
                        //httpRequestDemo()  // Llamada a la función suspendida
                        IdentificationRequestController.requestLogin("sap", "sap")
                        snackbarHostState.showSnackbar(
                            message = "Login successful!",
                            duration = SnackbarDuration.Short
                        )
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(
                            message = "Login failed: ${e.message}",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    ) {
        Text("Log in")
    }
}

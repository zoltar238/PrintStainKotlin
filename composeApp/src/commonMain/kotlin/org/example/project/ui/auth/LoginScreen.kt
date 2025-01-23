package org.example.project.ui.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.model.UserDto
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.service.loginUser
import org.example.project.ui.main.LoadingIndicator
import org.jetbrains.compose.resources.stringResource
import printstain.composeapp.generated.resources.Res
import printstain.composeapp.generated.resources.password_field
import printstain.composeapp.generated.resources.username_field


@Composable
fun LoginScreen(
    snackBarScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    snackBarColor: MutableState<Color>,
    navController: NavHostController,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var saveCredentials by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // SnackBarColors
    val primaryColor = MaterialTheme.colors.primary
    val errorColor = MaterialTheme.colors.error


    val commonModifier =
        Modifier.fillMaxWidth(1f).padding(horizontal = 40.dp).padding(vertical = 10.dp)

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

    // Save credentials checkbox
    Row(
        modifier = commonModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = saveCredentials,
            onCheckedChange = { saveCredentials = it }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Remember me")
    }

    Button(
        onClick = {
            if (username.isEmpty() || password.isEmpty()) {
                snackBarScope.launch {
                    snackBarColor.value = errorColor
                    snackbarHostState.showSnackbar(
                        message = "Please, fill all the fields",
                        duration = SnackbarDuration.Short
                    )
                }
            } else {
                isLoading = true
                snackBarScope.launch {
                    snackBarColor.value = primaryColor
                    try {
                        val serverResponse = withContext(context = Dispatchers.IO) {
                            loginUser(UserDto(username = username, password = password))
                        }
                        isLoading = false
                        if (serverResponse.success) {
                            // If credentials are set to be saved, save all user data
                            if (saveCredentials) {
                                PreferencesManager.saveUser(
                                    username = username,
                                    password = password,
                                    token = serverResponse.data
                                )
                            }
                            // If credentials arent set to be saved, save only token
                            else {
                                PreferencesManager.saveToken(serverResponse.data)
                            }
                            navController.navigate(route = "main_app_view")
                        } else {
                            snackBarColor.value = errorColor
                        }
                        snackbarHostState.showSnackbar(
                            message = serverResponse.response,
                            duration = SnackbarDuration.Short
                        )
                    } catch (e: Exception) {
                        snackBarColor.value = errorColor
                        isLoading = false
                        snackbarHostState.showSnackbar(
                            message = "Unexpected login error",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    ) {
        Text("Log in")
    }

    // Loading indicator
    if (isLoading) {
        LoadingIndicator()
    }
}
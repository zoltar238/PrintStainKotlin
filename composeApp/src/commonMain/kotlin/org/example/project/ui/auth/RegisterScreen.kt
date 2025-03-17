package org.example.project.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.apache.commons.validator.routines.EmailValidator
import org.example.project.model.dto.PersonDto
import org.example.project.viewModel.PersonViewModel
import org.jetbrains.compose.resources.stringResource
import printstain.composeapp.generated.resources.*

@Composable
fun RegisterScreen(
    onShowSnackBar: (String, Boolean) -> Unit,
    personViewModel: PersonViewModel,
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatedPassword by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var passwordCoincide by remember { mutableStateOf(true) }

    val commonModifier =
        Modifier.fillMaxWidth(1f).padding(horizontal = 40.dp).padding(vertical = 10.dp)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { newText -> name = newText },
                label = { Text(stringResource(Res.string.name_field)) },
                singleLine = true,
                modifier = commonModifier
            )

            OutlinedTextField(
                value = surname,
                onValueChange = { newText -> surname = newText },
                label = { Text(stringResource(Res.string.surname_field)) },
                singleLine = true,
                modifier = commonModifier
            )

            OutlinedTextField(
                value = username,
                onValueChange = { newText -> username = newText },
                label = { Text(stringResource(Res.string.username_field)) },
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
                singleLine = true,
                modifier = commonModifier,
                isError = !isEmailValid
            )

            OutlinedTextField(
                value = password,
                onValueChange = { newText ->
                    password = newText
                    passwordCoincide =
                        (password == repeatedPassword && repeatedPassword.isNotEmpty())
                },
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
                    passwordCoincide =
                        (password == repeatedPassword && repeatedPassword.isNotEmpty())
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
                    when {
                        name.isEmpty() || surname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty() -> {
                            onShowSnackBar("Please, fill all the fields", true)
                        }

                        !passwordCoincide -> {
                            onShowSnackBar("Passwords do not coincide", true)
                        }

                        !isEmailValid -> {
                            onShowSnackBar("Email is not valid", true)
                        }

                        else -> {
                            personViewModel.registerUser(
                                PersonDto(
                                    name = name,
                                    email = email,
                                    password = password,
                                    username = username,
                                    surname = surname,
                                    roles = listOf("USER")
                                )
                            )
                        }
                    }
                }
            ) {
                Text("Register")
            }
        }
    }
}
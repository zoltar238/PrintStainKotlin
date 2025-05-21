package org.example.project.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.apache.commons.validator.routines.EmailValidator
import org.example.project.model.dto.PersonDto
import org.example.project.ui.AppColors
import org.example.project.viewModel.PersonViewModel
import org.jetbrains.compose.resources.stringResource
import printstain.composeapp.generated.resources.*

@Composable
fun RegisterScreen(
    personViewModel: PersonViewModel,
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatedPassword by remember { mutableStateOf("") }

    // Track validation states for each field
    var isNameValid by remember { mutableStateOf(true) }
    var isSurnameValid by remember { mutableStateOf(true) }
    var isUsernameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isRepeatedPasswordValid by remember { mutableStateOf(true) }

    val commonModifier =
        Modifier.fillMaxWidth(1f).padding(horizontal = 40.dp).padding(vertical = 10.dp)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { newText ->
                    name = newText
                    isNameValid = newText.isNotEmpty()
                },
                label = { Text(stringResource(Res.string.name_field), color = AppColors.textOnBackgroundColor) },
                singleLine = true,
                modifier = commonModifier,
                isError = !isNameValid,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.textOnBackgroundColor,
                    focusedBorderColor = AppColors.primaryColor,
                    unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                    errorBorderColor = AppColors.errorColor,
                    errorLabelColor = AppColors.errorColor,
                    cursorColor = AppColors.primaryColor
                )
            )
            if (!isNameValid) {
                Text(
                    text = "Name cannot be empty",
                    color = AppColors.errorColor,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }

            OutlinedTextField(
                value = surname,
                onValueChange = { newText ->
                    surname = newText
                    isSurnameValid = newText.isNotEmpty()
                },
                label = { Text(stringResource(Res.string.surname_field), color = AppColors.textOnBackgroundColor) },
                singleLine = true,
                modifier = commonModifier,
                isError = !isSurnameValid,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.textOnBackgroundColor,
                    focusedBorderColor = AppColors.primaryColor,
                    unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                    errorBorderColor = AppColors.errorColor,
                    errorLabelColor = AppColors.errorColor,
                    cursorColor = AppColors.primaryColor
                )
            )
            if (!isSurnameValid) {
                Text(
                    text = "Surname cannot be empty",
                    color = AppColors.errorColor,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }

            OutlinedTextField(
                value = username,
                onValueChange = { newText ->
                    username = newText
                    isUsernameValid = newText.isNotEmpty()
                },
                label = { Text(stringResource(Res.string.username_field), color = AppColors.textOnBackgroundColor) },
                singleLine = true,
                modifier = commonModifier,
                isError = !isUsernameValid,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.textOnBackgroundColor,
                    focusedBorderColor = AppColors.primaryColor,
                    unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                    errorBorderColor = AppColors.errorColor,
                    errorLabelColor = AppColors.errorColor,
                    cursorColor = AppColors.primaryColor
                )
            )
            if (!isUsernameValid) {
                Text(
                    text = "Username cannot be empty",
                    color = AppColors.errorColor,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { newText ->
                    email = newText
                    isEmailValid = EmailValidator.getInstance().isValid(newText)
                },
                label = { Text(stringResource(Res.string.email_field), color = AppColors.textOnBackgroundColor) },
                singleLine = true,
                modifier = commonModifier,
                isError = !isEmailValid,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.textOnBackgroundColor,
                    focusedBorderColor = AppColors.primaryColor,
                    unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                    errorBorderColor = AppColors.errorColor,
                    errorLabelColor = AppColors.errorColor,
                    cursorColor = AppColors.primaryColor
                )
            )
            if (!isEmailValid) {
                Text(
                    text = "Invalid email address",
                    color = AppColors.errorColor,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }

            OutlinedTextField(
                value = password,
                onValueChange = { newText ->
                    password = newText
                    isPasswordValid = newText.isNotEmpty() && newText.length >= 6
                    isRepeatedPasswordValid = repeatedPassword.isEmpty() || newText == repeatedPassword
                },
                label = { Text(stringResource(Res.string.password_field), color = AppColors.textOnBackgroundColor) },
                placeholder = { Text("*************", color = AppColors.textOnBackgroundSecondaryColor) },
                singleLine = true,
                modifier = commonModifier,
                visualTransformation = PasswordVisualTransformation(),
                isError = !isPasswordValid,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.textOnBackgroundColor,
                    focusedBorderColor = AppColors.primaryColor,
                    unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                    errorBorderColor = AppColors.errorColor,
                    errorLabelColor = AppColors.errorColor,
                    cursorColor = AppColors.primaryColor
                )
            )
            if (!isPasswordValid) {
                Text(
                    text = if (password.isEmpty()) "Password cannot be empty" else "Password must be at least 6 characters",
                    color = AppColors.errorColor,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }

            OutlinedTextField(
                value = repeatedPassword,
                onValueChange = { newText ->
                    repeatedPassword = newText
                    isRepeatedPasswordValid = newText == password && newText.isNotEmpty()
                },
                label = {
                    Text(
                        stringResource(Res.string.repeat_password_field),
                        color = AppColors.textOnBackgroundColor
                    )
                },
                placeholder = { Text("*************", color = AppColors.textOnBackgroundSecondaryColor) },
                singleLine = true,
                modifier = commonModifier,
                visualTransformation = PasswordVisualTransformation(),
                isError = !isRepeatedPasswordValid,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.textOnBackgroundColor,
                    focusedBorderColor = AppColors.primaryColor,
                    unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                    errorBorderColor = AppColors.errorColor,
                    errorLabelColor = AppColors.errorColor,
                    cursorColor = AppColors.primaryColor
                )
            )
            if (!isRepeatedPasswordValid) {
                Text(
                    text = "Passwords do not match",
                    color = AppColors.errorColor,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }

            Button(
                onClick = {
                    // Validate all fields before registration
                    isNameValid = name.isNotEmpty()
                    isSurnameValid = surname.isNotEmpty()
                    isUsernameValid = username.isNotEmpty()
                    isEmailValid = EmailValidator.getInstance().isValid(email)
                    isPasswordValid = password.isNotEmpty()
                    isRepeatedPasswordValid = password == repeatedPassword && password.isNotEmpty()

                    // Only proceed if all validations pass
                    if (isNameValid && isSurnameValid && isUsernameValid &&
                        isEmailValid && isPasswordValid && isRepeatedPasswordValid
                    ) {
                        personViewModel.registerUser(
                            PersonDto(
                                name = name,
                                email = email,
                                password = password,
                                username = username,
                                surname = surname,
                                roles = listOf("ADMIN")
                            )
                        )
                    }
                },
                modifier = commonModifier,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = AppColors.primaryColor,
                    contentColor = AppColors.textOnPrimaryColor
                )
            ) {
                Text("Register")
            }
        }
    }
}
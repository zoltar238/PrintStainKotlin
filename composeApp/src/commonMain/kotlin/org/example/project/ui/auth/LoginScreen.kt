package org.example.project.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.example.project.model.dto.LoginDto
import org.example.project.ui.AppColors
import org.example.project.viewModel.PersonViewModel
import org.jetbrains.compose.resources.stringResource
import printstain.composeapp.generated.resources.Res
import printstain.composeapp.generated.resources.password_field
import printstain.composeapp.generated.resources.username_field

@Composable
fun LoginScreen(
    personViewModel: PersonViewModel,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var saveCredentials by remember { mutableStateOf(false) }

    // Validation states
    var isUsernameValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }

    val commonModifier =
        Modifier.fillMaxWidth(1f).padding(horizontal = 40.dp).padding(vertical = 10.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                value = password,
                onValueChange = { newText ->
                    password = newText
                    isPasswordValid = newText.isNotEmpty()
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
                    text = "Password cannot be empty",
                    color = AppColors.errorColor,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }

            // Save credential checkbox
            Row(
                modifier = commonModifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = saveCredentials,
                    onCheckedChange = { saveCredentials = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remember me",
                    color = AppColors.textOnBackgroundColor
                )
            }

            Button(
                onClick = {
                    // Validate fields
                    isUsernameValid = username.isNotEmpty()
                    isPasswordValid = password.isNotEmpty()

                    // Only proceed if both fields are valid
                    if (isUsernameValid && isPasswordValid) {
                        personViewModel.loginUser(
                            LoginDto(username = username, password = password),
                            saveCredentials
                        )
                    }
                },
                modifier = commonModifier,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = AppColors.primaryColor,
                    contentColor = AppColors.textOnPrimaryColor
                )
            ) {
                Text(
                    "Log in",
                    color = AppColors.textOnPrimaryColor
                )
            }
        }
    }
}
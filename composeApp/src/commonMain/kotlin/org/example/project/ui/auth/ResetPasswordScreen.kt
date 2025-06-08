package org.example.project.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.example.project.ui.AppColors
import org.example.project.ui.component.ReturnButton
import org.example.project.ui.navigation.AppModule.personViewModel

@Composable
fun ResetPasswordScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Validation states
    var isUsernameValid by remember { mutableStateOf(true) }
    var isNewPasswordValid by remember { mutableStateOf(true) }
    var isConfirmPasswordValid by remember { mutableStateOf(true) }
    var doPasswordsMatch by remember { mutableStateOf(true) }

    val commonModifier = Modifier
        .fillMaxWidth(1f)
        .padding(horizontal = 40.dp)
        .padding(vertical = 10.dp)
    MaterialTheme {
        Scaffold(
            floatingActionButtonPosition = FabPosition.Start,
            floatingActionButton = {
                ReturnButton(
                    navController = navController
                )
            })
        { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().background(AppColors.backgroundColor).padding(innerPadding)) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Reset Password",
                            color = AppColors.textOnBackgroundColor,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                isUsernameValid = it.isNotEmpty()
                            },
                            label = { Text("Username", color = AppColors.textOnBackgroundColor) },
                            placeholder = { Text("Username", color = AppColors.textOnBackgroundSecondaryColor) },
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
                            value = newPassword,
                            onValueChange = { newText ->
                                newPassword = newText
                                isNewPasswordValid = newText.length >= 6
                                doPasswordsMatch = newText == confirmPassword
                            },
                            label = { Text("New Password", color = AppColors.textOnBackgroundColor) },
                            placeholder = { Text("*************", color = AppColors.textOnBackgroundSecondaryColor) },
                            singleLine = true,
                            modifier = commonModifier,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = !isNewPasswordValid,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = AppColors.textOnBackgroundColor,
                                focusedBorderColor = AppColors.primaryColor,
                                unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                                errorBorderColor = AppColors.errorColor,
                                errorLabelColor = AppColors.errorColor,
                                cursorColor = AppColors.primaryColor
                            )
                        )
                        if (!isNewPasswordValid) {
                            Text(
                                text = "Password must be at least 6 characters long",
                                color = AppColors.errorColor,
                                modifier = Modifier.padding(start = 40.dp)
                            )
                        }

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { newText ->
                                confirmPassword = newText
                                isConfirmPasswordValid = newText.length >= 6
                                doPasswordsMatch = newText == newPassword
                            },
                            label = { Text("Confirm Password", color = AppColors.textOnBackgroundColor) },
                            placeholder = { Text("*************", color = AppColors.textOnBackgroundSecondaryColor) },
                            singleLine = true,
                            modifier = commonModifier,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = !isConfirmPasswordValid || !doPasswordsMatch,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = AppColors.textOnBackgroundColor,
                                focusedBorderColor = AppColors.primaryColor,
                                unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                                errorBorderColor = AppColors.errorColor,
                                errorLabelColor = AppColors.errorColor,
                                cursorColor = AppColors.primaryColor
                            )
                        )
                        if (!doPasswordsMatch) {
                            Text(
                                text = "Passwords do not match",
                                color = AppColors.errorColor,
                                modifier = Modifier.padding(start = 40.dp)
                            )
                        }

                        Button(
                            onClick = {
                                // Validate fields
                                isNewPasswordValid = newPassword.length >= 6
                                isConfirmPasswordValid = confirmPassword.length >= 6
                                doPasswordsMatch = newPassword == confirmPassword

                                // Only proceed if all validations pass
                                if (isNewPasswordValid && isConfirmPasswordValid && doPasswordsMatch && username.isNotEmpty()) {
                                    personViewModel.resetPassword(username, newPassword)
                                }
                            },
                            modifier = commonModifier.padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.primaryColor,
                                contentColor = AppColors.textOnPrimaryColor
                            )
                        ) {
                            Text(
                                "Reset Password",
                                color = AppColors.textOnPrimaryColor
                            )
                        }
                    }
                }
            }
        }
    }
}
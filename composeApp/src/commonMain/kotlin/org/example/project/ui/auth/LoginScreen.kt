package org.example.project.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.example.project.model.dto.LoginDto
import org.example.project.viewModel.PersonViewModel
import org.jetbrains.compose.resources.stringResource
import printstain.composeapp.generated.resources.Res
import printstain.composeapp.generated.resources.password_field
import printstain.composeapp.generated.resources.username_field

@Composable
fun LoginScreen(
    onShowSnackBar: (String, Boolean) -> Unit,
    personViewModel: PersonViewModel,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var saveCredentials by remember { mutableStateOf(false) }

    val commonModifier =
        Modifier.fillMaxWidth(1f).padding(horizontal = 40.dp).padding(vertical = 10.dp)

    OutlinedTextField(
        value = username,
        onValueChange = { newText -> username = newText },
        label = { Text(stringResource(Res.string.username_field)) },
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
        Text(text = "Remember me")
    }

    Button(
        onClick = {
            if (username.isEmpty() || password.isEmpty()) {
                onShowSnackBar("Please, fill all the fields", true)
            } else {
                personViewModel.loginUser(LoginDto(username = username, password = password), saveCredentials)
            }
        })
    {
        Text("Log in")
    }
}

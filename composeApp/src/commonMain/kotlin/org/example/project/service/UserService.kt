package org.example.project.service

import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.logging.LoggingTags
import org.example.project.model.UserDto
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.repository.responseHandler

fun registerUser(userDto: UserDto): ResponseApi<String> {
    // Receive response from server and return it
    return responseHandler(
        "Register user",
        LoggingTags.UserRegistration.name,
        "String"
    ) { ClientController.userController.registerUser(userDto) }
}

fun loginUser(userDto: UserDto): ResponseApi<String> {
    // Receive response from server and return it
    return responseHandler(
        "Login user",
        LoggingTags.Userlogin.name,
        "String"
    ) { ClientController.userController.loginUser(userDto) }
}

suspend fun autoLogin(): Boolean {
    // Get saved values
    val username = PreferencesManager.getUsername()
    val password = PreferencesManager.getPassword()

    // If credentials are not saved, return false
    return if (username == null || password == null) {
        false
    } else {
        val serverResponse = loginUser(UserDto(username = username, password = password))
        // If login is successful, save token and return true, else return false
        return when (serverResponse.success) {
            true -> {
                PreferencesManager.saveToken(serverResponse.data)
                true
            }

            else -> {
                false
            }
        }
    }
}



package org.example.project.service

import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.LoginDto
import org.example.project.model.dto.PersonDto
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.repository.responseHandler

fun registerUser(personDto: PersonDto): ResponseApi<String> {
    // Receive response from server and return it
    return responseHandler(
        "Register user",
        ProcessTags.UserRegistration.name,
        "String"
    ) { ClientController.userController.registerUser(personDto) }
}

fun loginUser(loginDto: LoginDto): ResponseApi<String> {
    // Receive response from server and return it
    return responseHandler(
        "Login user",
        ProcessTags.Userlogin.name,
        "String"
    ) { ClientController.userController.loginUser(loginDto) }
}

suspend fun autoLogin(): Boolean {
    // Get saved values
    val username = PreferencesManager.getUsername()
    val password = PreferencesManager.getPassword()

    // If credentials are not saved, return false
    return if (username == null || password == null) {
        false
    } else {
        val serverResponse = loginUser(LoginDto(username = username, password = password))
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



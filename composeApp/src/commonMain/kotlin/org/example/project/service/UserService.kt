package org.example.project.service

import org.example.project.model.UserDto
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.repository.UserRepoHttpImp

fun registerUser(userDto: UserDto): ResponseApi<String> {
    // Receive response from server
    val serverResponse = UserRepoHttpImp.registerUser(userDto)
    return serverResponse!!
}

fun loginUser(userDto: UserDto): ResponseApi<String> {
    // Receive response from server
    val serverResponse: ResponseApi<String>? = UserRepoHttpImp.loginUser(userDto)
    return serverResponse!!
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
        return when(serverResponse.success){
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



package org.example.project.controller

import org.example.project.model.UserDto
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.service.loginUser

suspend fun loginController(): Boolean {
    // Get saved values
    val username = PreferencesManager.getUsername()
    val password = PreferencesManager.getPassword()

    println(username)
    // if credentials are not saved, return false
    if (username == null || password == null) {
        return false
    } else {
        val (success, data) = loginUser(UserDto(username = username, password = password))
        // If login is successful, save token and return true, else return false
        if (success) {
            PreferencesManager.saveToken(data)
            return true
        } else {
            return false
        }
    }
}
package org.example.project.controller

import org.example.project.entity.ServerDto
import org.example.project.service.httpGetRequestDemo
import org.example.project.service.httpPostRequestDemo

data class Token(val token: String)

object IdentificationRequestController {

    suspend fun requestLogin(username: String, password: String) {
        val url = "http://localhost:8080/person/login"
        val result: ServerDto<Token>? = httpPostRequestDemo(url, username, password)

        // Check result
        if (result != null && result.success) {
            println("User fetched: ${result.data}")
        } else {
            println("Failed to fetch user or server returned an error.")
        }
    }
}
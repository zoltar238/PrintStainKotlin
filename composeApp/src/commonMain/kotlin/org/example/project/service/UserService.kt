package org.example.project.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.logging.AppLogger
import org.example.project.model.User
import org.example.project.persistence.repository.UserRepoHttpImp

// TODO: improve null response
fun registerUser(user: User): Pair<Boolean, String> {
    // Create mapper and read JSON
    val objectMapper = jacksonObjectMapper()
    val rootNode: JsonNode = objectMapper.readTree(UserRepoHttpImp.registerUser(user))


    try {
        val success = rootNode.get("success")?.asBoolean() ?: false
        val response = rootNode.get("response")?.asText() ?: "Unknown response"
        val data = rootNode.get("data")?.asText() ?: "Unknown response"

        // Log data received from server
        AppLogger.i("UserRegistration", data)

        // Return registration status
        return when (response) {
            "EMAIL_ALREADY_REGISTERED" -> {
                success to "This email is already registered"
            }
            "USERNAME_ALREADY_REGISTERED" -> {
                success to "This username is already registered"
            }
            "UNEXPECTED_ERROR" -> {
                success to "Unexpected error"
            }
            "OK" -> {
                success to "New user registered successfully"
            }
            else -> {
                false to "Unexpected error"
            }
        }
    } catch (e: Exception) {
        AppLogger.e("ServerConnection", "Could not connect to server", e)
        return false to "Connection error"
    }
}

// TODO: Improve login system, create better responses for each possible failure type

fun loginUser(user: User): Pair<Boolean, String> {
    // Create mapper and read JSON
    val objectMapper = jacksonObjectMapper()
    val rootNode: JsonNode =
        objectMapper.readTree(UserRepoHttpImp.loginUser(user))

    try {
        val success = rootNode.get("success")?.asBoolean() ?: false
        val response = rootNode.get("response")?.asText() ?: "Unknown response"
        val data = rootNode.get("data")?.asText() ?: "Unknown response"

        AppLogger.i("UserRegistration", response)

        return if (response == "LOGIN_CORRECT"){
            success to data
        } else{
            success to "Wrong data"
        }
    } catch (e: Exception) {
        AppLogger.e("ServerConnection", "Could not connect to server", e)
        return false to "Connection error"
    }
}
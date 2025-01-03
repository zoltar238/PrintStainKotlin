package org.example.project.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.logging.AppLogger
import org.example.project.model.UserDto
import org.example.project.persistence.repository.UserRepoHttpImp

// TODO: improve null response
fun registerUser(userDto: UserDto): Pair<Boolean, String> {
    // Create mapper and read JSON
    val objectMapper = jacksonObjectMapper()
    val rootNode: JsonNode = objectMapper.readTree(UserRepoHttpImp.registerUser(userDto))

    // Process received information
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

fun loginUser(userDto: UserDto): Pair<Boolean, String> {
    val objectMapper = jacksonObjectMapper()
    val jsonResponse = UserRepoHttpImp.loginUser(userDto)
    print(jsonResponse.data)
    //println("respuesta: $jsonResponse")

    /*
    try {
        // Parseo del JSON
        val rootNode: JsonNode = objectMapper.readTree(jsonResponse)

        // Extrae los valores del JSON
        val success = rootNode.get("success")?.asBoolean() ?: false
        val responseMessage = rootNode.get("response")?.asText() ?: "Unknown response"
        val token = rootNode.get("data")?.asText() ?: "Unknown token"

        // Log de éxito o error
        AppLogger.i("UserRegistration", responseMessage)

        // Verifica el mensaje de respuesta
        return if (responseMessage == "LOGIN_CORRECT") {
            success to token
        } else {
            success to "Wrong data"
        }
    } catch (e: Exception) {
        // Manejo de errores
        AppLogger.e("ServerConnection", "Error parsing JSON or server response", e)
        return false to "Invalid JSON format"
    }
     */
    return false to "Invalid JSON format"

}


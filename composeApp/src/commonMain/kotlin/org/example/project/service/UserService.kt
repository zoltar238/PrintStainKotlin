package org.example.project.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.logging.AppLogger
import org.example.project.model.UserDto
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.repository.UserRepoHttpImp

fun registerUser(userDto: UserDto): ResponseApi<String> {
    // Receive response from server
    val serverResponse = UserRepoHttpImp.registerUser(userDto)
    println(serverResponse.success)
    return serverResponse
}

// TODO: Improve login system, create better responses for each possible failure type
fun loginUser(userDto: UserDto): Pair<Boolean, String> {
    val objectMapper = jacksonObjectMapper()

    // Serialize response object to json
    val jsonResponse = UserRepoHttpImp.loginUser(userDto)
    val jsonString = objectMapper.writeValueAsString(jsonResponse)

    try {
        // Parse JSON
        val rootNode: JsonNode = objectMapper.readTree(jsonString)

        // Extract values from JSON
        val success = rootNode.get("success")?.asBoolean() ?: false
        val responseMessage = rootNode.get("response")?.asText() ?: "Unknown response"
        val token = rootNode.get("data")?.asText() ?: "Unknown token"

        print("este es el token recivido: $token")

        // Log response from server
        AppLogger.i("Authentication", responseMessage)

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
}


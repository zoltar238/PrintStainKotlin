package org.example.project.service

import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.model.dto.LoginDto
import org.example.project.model.dto.PersonDto
import org.example.project.persistence.preferences.PreferencesDaoImpl

class PersonService() {

    suspend fun registerUser(personDto: PersonDto): ResponseApi<String> {
        val processCode = "000001"
        val processName = "User registration"

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to register user with username: '${personDto.username}', email: '${personDto.email}', roles: '${personDto.roles}'.")
        AppLogger.d("[DBG-$processCode: $processName] -> PersonDto details: $personDto")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Calling server to register user.")
            val serverResponse = responseHandler { ClientController.userController.registerUser(personDto) }
            AppLogger.d("[DBG-$processCode: $processName] -> Server response received. Success: ${serverResponse.success}, Response: ${serverResponse.response}")

            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully registered user with username: '${personDto.username}'. Server response: ${serverResponse.data}")
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Error registering user with username: '${personDto.username}': ${serverResponse.response}.")
            }
            serverResponse
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error registering user with username: '${personDto.username}': ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(success = false, response = "Unexpected error during registration: ${e.localizedMessage}", data = null)
        }
    }

    suspend fun loginUser(loginDto: LoginDto): ResponseApi<String> {
        val processCode = "000002"
        val processName = "User login"

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to login user with username: '${loginDto.username}'.")
        AppLogger.d("[DBG-$processCode: $processName] -> LoginDto details: $loginDto")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Calling server to login user.")
            val serverResponse = responseHandler { ClientController.userController.loginUser(loginDto) }
            AppLogger.d("[DBG-$processCode: $processName] -> Server response received. Success: ${serverResponse.success}, Response: ${serverResponse.response}, Data: ${serverResponse.data}") // Data podría ser el token

            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully logged in user with username: '${loginDto.username}'.")
                // Considerar guardar el token aquí si serverResponse.data es el token
                // serverResponse.data?.let { token -> saveToken(token) }
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Error logging in user with username: '${loginDto.username}': ${serverResponse.response}.")
            }
            serverResponse
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error logging in user with username: '${loginDto.username}': ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(success = false, response = "Unexpected error during login: ${e.localizedMessage}", data = null)
        }
    }

    suspend fun saveUserCredentials(username: String, password: String, token: String) {
        val processCode = "000035" // Nuevo código de proceso
        val processName = "Save user credentials"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to save credentials for username: '$username'.")
        // No se loguea la contraseña directamente por seguridad, pero sí el token (o parte de él si es muy largo)
        AppLogger.d("[DBG-$processCode: $processName] -> Username: '$username', Token: '${token.take(10)}...' (first 10 chars).")
        try {
            PreferencesDaoImpl.saveUser(
                username = username,
                password = password, // La contraseña se pasa pero no se loguea
                token = token
            )
            AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully saved credentials for username: '$username'.")
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error saving credentials for username '$username': ${e.localizedMessage}.",
                throwable = e
            )
            // Considerar cómo manejar esta excepción, ya que el método no devuelve ResponseApi
        }
    }

    suspend fun saveToken(token: String) {
        val processCode = "000036" // Nuevo código de proceso
        val processName = "Save token"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to save token.")
        AppLogger.d("[DBG-$processCode: $processName] -> Token: '${token.take(10)}...' (first 10 chars).")
        try {
            PreferencesDaoImpl.saveToken(token)
            AppLogger.i("[MSG-$processCode: $processName - End of process] -> Token saved successfully.")
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error saving token: ${e.localizedMessage}.",
                throwable = e
            )
        }
    }

    suspend fun getUserCredentials(): Pair<String?, String?> {
        val processCode = "000037" // Nuevo código de proceso
        val processName = "Get user credentials"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to retrieve user credentials.")
        return try {
            val username = PreferencesDaoImpl.getUsername()
            val password = PreferencesDaoImpl.getPassword() // Recuperar la contraseña, pero no loguearla
            AppLogger.d("[DBG-$processCode: $processName] -> Retrieved username: '$username'. Password retrieved (not logged).")
            AppLogger.i("[MSG-$processCode: $processName - End of process] -> User credentials retrieved.")
            Pair(username, password)
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error retrieving user credentials: ${e.localizedMessage}.",
                throwable = e
            )
            Pair(null, null) // Devolver nulos en caso de error
        }
    }

    suspend fun deleteUser(): ResponseApi<String> {
        val processCode = "000003"
        val processName = "User deletion"

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to delete current user.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token for deletion.")
            val token = PreferencesDaoImpl.getToken()
            if (token == null) {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Cannot delete user: Token not found.")
                return ResponseApi(success = false, response = "Authentication token not found. Cannot delete user.", data = null)
            }
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained. Calling server to delete user.")

            val serverResponse = responseHandler {
                ClientController.userController.deleteUser(
                    token = "Bearer $token"
                )
            }
            AppLogger.d("[DBG-$processCode: $processName] -> Server response received. Success: ${serverResponse.success}, Response: ${serverResponse.response}")

            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> User deleted successfully on server. Server response: ${serverResponse.data}")
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Error deleting user on server: ${serverResponse.response}.")
            }
            serverResponse
        } catch (e: Exception) {
            // El error original en el log decía "Starting process"
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error deleting user: ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(success = false, response = "Unexpected error during user deletion: ${e.localizedMessage}", data = null)
        }
    }

    suspend fun resetPassword(username: String, newPassword: String): ResponseApi<String> {
        val processCode = "000004"
        val processName = "User password reset"

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to reset password for user: '$username'.")
        // No loguear newPassword
        AppLogger.d("[DBG-$processCode: $processName] -> Username for password reset: '$username'.")
        return try {
            val personDto = PersonDto(
                username = username,
                password = newPassword // Se pasa al DTO pero no se loguea
            )
            AppLogger.d("[DBG-$processCode: $processName] -> PersonDto for password reset created. Calling server.")

            val serverResponse = responseHandler {
                ClientController.userController.resetPassword(user = personDto)
            }
            AppLogger.d("[DBG-$processCode: $processName] -> Server response received. Success: ${serverResponse.success}, Response: ${serverResponse.response}")

            if (serverResponse.success) {
                // El error original en el log decía "Starting process"
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully reset password for user: '$username'. Server response: ${serverResponse.data}")
            } else {
                // El error original en el log decía "Starting process"
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Error resetting password for user: '$username': ${serverResponse.response}.")
            }
            serverResponse
        } catch (e: Exception) {
            // El error original en el log decía "Starting process"
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error resetting password for user: '$username': ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(success = false, response = "Unexpected error during password reset: ${e.localizedMessage}", data = null)
        }
    }
}
package org.example.project.service

import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.LoginDto
import org.example.project.model.dto.PersonDto
import org.example.project.persistence.database.PersonDao
import org.example.project.persistence.database.PersonDaoImpl
import org.example.project.persistence.preferences.PreferencesDaoImpl

class PersonService(
    database: PrintStainDatabase,
) {
    private val personDao: PersonDao = PersonDaoImpl(database)

    suspend fun registerUser(personDto: PersonDto): ResponseApi<String> {
        return try {
            responseHandler(
                "Register user",
                ProcessTags.UserRegistration.name,
            ) { ClientController.userController.registerUser(personDto) }
        } catch (e: Exception) {
            AppLogger.e(
                "",
                """
                    Process: ${ProcessTags.Userlogin.name}.
                    Status: Unexpected internal error registering user.
                """.trimIndent(),
                e
            )
            ResponseApi(success = false, response = "Error: ${e.localizedMessage}", null)
        }
    }

    suspend fun loginUser(loginDto: LoginDto): ResponseApi<String> {
        return try {
            responseHandler(
                "Login user",
                ProcessTags.Userlogin.name,
            ) { ClientController.userController.loginUser(loginDto) }
        } catch (e: Exception) {
            AppLogger.e(
                "",
                """
                    Process: ${ProcessTags.Userlogin.name}.
                    Status: Unexpected internal error login user.
                """.trimIndent(),
                e
            )
            ResponseApi(success = false, response = "Error: ${e.localizedMessage}", null)
        }
    }

    suspend fun saveUserCredentials(username: String, password: String, token: String) {
        PreferencesDaoImpl.saveUser(
            username = username,
            password = password,
            token = token
        )
    }

    suspend fun saveToken(token: String) {
        PreferencesDaoImpl.saveToken(token)
    }

    suspend fun getUserCredentials(): Pair<String?, String?> {
        val username = PreferencesDaoImpl.getUsername()
        val password = PreferencesDaoImpl.getPassword()
        return Pair(username, password)
    }
}

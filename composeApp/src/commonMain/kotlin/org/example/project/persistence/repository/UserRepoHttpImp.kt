package org.example.project.persistence.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import org.example.project.logging.AppLogger
import org.example.project.logging.LoggingTags
import org.example.project.model.UserDto
import org.example.project.persistence.network.ClientApi
import org.example.project.persistence.network.HttpStatusUtil
import org.example.project.persistence.network.ResponseApi

object UserRepoHttpImp : UserRepo {

    override suspend fun registerUser(userDto: UserDto): ResponseApi<String>? {
        return try {
            // Connect to server
            AppLogger.i(LoggingTags.UserRegistration.name, "Attempting connection")
            val response = ClientApi.userApi.registerUser(userDto)

            if (response.isSuccessful) {
                // Log successful registration and return body
                AppLogger.i(
                    LoggingTags.UserRegistration.name,
                    "Status: ${response.code()} ${HttpStatusUtil.getStatusName(response.code())}," +
                            " Response: ${response.body()?.response!!}"
                )
                response.body()
            } else {
                // Log failed registration and return errorBody
                val errorBody = response.errorBody()?.string()
                val gson = Gson()
                val type = object : TypeToken<ResponseApi<String>>() {}.type
                val errorResponse: ResponseApi<String> = gson.fromJson(errorBody, type)

                AppLogger.w(
                    LoggingTags.UserRegistration.name,
                    "Status: ${response.code()} ${HttpStatusUtil.getStatusName(response.code())}," +
                            " Response: ${errorResponse.response}"
                )
                errorResponse
            }
        } catch (e: Exception) {
            // Network error -> log error and return error body
            AppLogger.e(
                LoggingTags.UserRegistration.name,
                "Status: 503 ${HttpStatusUtil.getStatusName(503)}," +
                        " Response: Error connecting to server}",
                e
            )
            return ResponseApi(false, "Error connecting to server", "Error connecting to server")
        }
    }

    override fun loginUser(userDto: UserDto): ResponseApi<String> {
        return runBlocking {
            ClientApi.userApi.loginUser(userDto)
        }
    }
}

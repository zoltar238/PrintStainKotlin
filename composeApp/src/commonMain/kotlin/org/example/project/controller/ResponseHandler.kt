package org.example.project.controller

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import retrofit2.Response

suspend inline fun <reified T> responseHandler(
    crossinline apiFunction: suspend () -> Response<ResponseApi<T>>,
): ResponseApi<T> {
    return try {
        val response = apiFunction()

        if (response.isSuccessful) {
            response.body() ?: ResponseApi(false, "Empty response body", null)
        } else {
            when (response.code()) {
                401 -> ResponseApi(false, "Unauthorized access", null)
                else -> {
                    // Parsear respuesta de error personalizada del servidor
                    val errorBody = response.errorBody()?.string()
                    val json = Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        isLenient = true
                    }

                    try {
                        errorBody?.let {
                            json.decodeFromString<ResponseApi<T>>(it)
                        } ?: ResponseApi(false, "Error parsing error response", null)
                    } catch (e: Exception) {
                        ResponseApi(false, "Internal error parsing response: ${e.localizedMessage}", null)
                    }
                }
            }
        }
    } catch (e: java.net.ConnectException) {
        ResponseApi(false, "Connection error: ${e.localizedMessage}", null)
    } catch (e: java.net.SocketTimeoutException) {
        ResponseApi(false, "Request timeout: ${e.localizedMessage}", null)
    } catch (e: Exception) {
        ResponseApi(false, "Unexpected error: ${e.localizedMessage}", null)
    }
}
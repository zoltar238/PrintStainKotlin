package org.example.project.controller

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import retrofit2.Response

@OptIn(DelicateCoroutinesApi::class)
inline fun <reified T> responseHandler(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline apiFunction: suspend () -> Response<ResponseApi<T>>,
): ResponseApi<T> {
    val deferredResult = CompletableDeferred<ResponseApi<T>>()

    GlobalScope.launch(dispatcher) {
        try {
            val response: Response<ResponseApi<T>> = apiFunction()
            if (response.isSuccessful) {
                val responseBody =
                    response.body() ?: ResponseApi(false, "Error receiving response from server", null)
                deferredResult.complete(responseBody as ResponseApi<T>)
            } else {
                when (response.code()) {
                    401 -> {
                        deferredResult.complete(ResponseApi(false, "Unauthorized access", null))
                    }
                    else -> {
                        val errorBody = response.errorBody()?.string()
                        val json = Json {
                            ignoreUnknownKeys = true
                            coerceInputValues = true
                            isLenient = true
                        }
                        val errorResponse: ResponseApi<T> = try {
                            errorBody?.let { json.decodeFromString(it) }
                                ?: ResponseApi(false, "Error parsing error response", null)
                        } catch (e: Exception) {
                            // En lugar de throw, completa con error
                            ResponseApi(false, "Internal error parsing response: ${e.localizedMessage}", null)
                        }
                        deferredResult.complete(errorResponse)
                    }
                }
            }
        } catch (e: java.net.ConnectException) {
            // Completa con error en lugar de lanzar excepción
            deferredResult.complete(
                ResponseApi(false, "Error connecting to server: ${e.localizedMessage}", null)
            )
        } catch (e: Exception) {
            // Completa con error en lugar de lanzar excepción
            deferredResult.complete(
                ResponseApi(false, "The app experienced an unexpected internal error: ${e.localizedMessage}", null)
            )
        }
    }

    return runBlocking { deferredResult.await() }
}
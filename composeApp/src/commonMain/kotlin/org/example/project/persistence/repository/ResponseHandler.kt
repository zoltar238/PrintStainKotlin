package org.example.project.persistence.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.example.project.controller.ResponseApi
import org.example.project.logging.AppLogger
import org.example.project.util.HttpStatusUtil
import retrofit2.Response

@OptIn(DelicateCoroutinesApi::class)
fun <T> responseHandler(
    process: String,
    processTag: String,
    returnType: String,
    apiFunction: suspend () -> Response<ResponseApi<T>>,
): ResponseApi<T> {
    val deferredResult = CompletableDeferred<ResponseApi<T>>()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Connect to server
            AppLogger.i(
                processTag,
                """Process: $process.
                Status: Attempting connection.""".trimIndent()
            )
            val response: Response<ResponseApi<T>> = apiFunction()

            if (response.isSuccessful) {
                // Log successful attempt and return body
                AppLogger.i(
                    processTag,
                    """Process: $process.
                    Status: ${response.code()} ${HttpStatusUtil.getStatusName(response.code())}.
                    Response: ${response.body()?.response ?: "No response body"}.""".trimIndent()
                )
                // Ensure the body is not null
                val responseBody =
                    response.body() ?: createErrorResponse(
                        "Error receiving response from server",
                        returnType
                    )
                deferredResult.complete(responseBody)
            } else {
                // Register failed attempt and return error body
                val errorBody = response.errorBody()?.string()
                val gson = Gson()
                val type = object : TypeToken<ResponseApi<T>>() {}.type

                // Error-free json parsing
                val errorResponse: ResponseApi<T> = try {
                    gson.fromJson(errorBody, type)
                } catch (e: Exception) {
                    AppLogger.e(
                        processTag,
                        """Process: $process.
                        Status: 422 ${HttpStatusUtil.getStatusName(422)}.
                        Response: Error parsing error response.""".trimIndent(),
                        e
                    )
                    createErrorResponse("Internal error parsing response", returnType)
                }

                AppLogger.w(
                    processTag,
                    """Process: $process.
                    Status: ${response.code()} ${HttpStatusUtil.getStatusName(response.code())}.
                    Response: ${errorResponse.response}.""".trimIndent()
                )
                deferredResult.complete(errorResponse)
            }
        } catch (e: java.net.ConnectException) {
            // Network error -> log error and return error response
            AppLogger.e(
                processTag,
                """Process: $process.
                Status: 503 ${HttpStatusUtil.getStatusName(503)}.
                Response: Error connecting to server.""".trimIndent(),
                e
            )
            deferredResult.complete(createErrorResponse("Error connecting to server", returnType))
        } catch (e: Exception) {
            // Other exceptions -> log the error and return error response
            AppLogger.e(
                processTag,
                """Process: $process.
                Status: Unknown error.
                Response: Internal app error.""".trimIndent(),
                e
            )
            deferredResult.complete(createErrorResponse("Internal app error", returnType))
        }
    }

    return runBlocking { deferredResult.await() }
}

// Auxiliary function to create error response
@Suppress("UNCHECKED_CAST")
private fun <T> createErrorResponse(response: String, returnType: String): ResponseApi<T> {
    return when (returnType) {
        "String" -> ResponseApi(
            false,
            response,
            response
        ) as ResponseApi<T>

        "List" -> ResponseApi(
            false,
            response,
            emptyList<T>()
        ) as ResponseApi<T>

        else -> ResponseApi(
            false,
            response,
            null
        ) as ResponseApi<T>
    }
}
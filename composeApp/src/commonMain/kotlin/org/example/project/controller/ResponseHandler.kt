package org.example.project.controller

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.example.project.logging.AppLogger
import org.example.project.util.HttpStatusUtil
import retrofit2.Response

@OptIn(DelicateCoroutinesApi::class)
inline fun <reified T> responseHandler(
    process: String,
    processTag: String,
    returnType: String,
    crossinline apiFunction: suspend () -> Response<ResponseApi<T>>,
): ResponseApi<T> {
    val deferredResult = CompletableDeferred<ResponseApi<T>>()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            AppLogger.i(
                processTag,
                """Process: $process.
                Status: Attempting connection.""".trimIndent()
            )
            val response: Response<ResponseApi<T>> = apiFunction()

            if (response.isSuccessful) {
                AppLogger.i(
                    processTag,
                    """Process: $process.
                    Status: ${response.code()} ${HttpStatusUtil.getStatusName(response.code())}.
                    Response: ${response.body()?.response ?: "No response body"}.""".trimIndent()
                )
                val responseBody =
                    response.body() ?: createErrorResponse("Error receiving response from server", returnType)
                deferredResult.complete(responseBody)
            } else {
                when (response.code()) {
                    401 -> {
                        AppLogger.w(
                            processTag,
                            """Process: $process.
                            Status: 401 Unauthorized.
                            Response: Unauthorized access.""".trimIndent()
                        )
                        deferredResult.complete(createErrorResponse("Unauthorized", returnType))
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
                                ?: createErrorResponse("Error parsing error response", returnType)
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
                }
            }
        } catch (e: java.net.ConnectException) {
            AppLogger.e(
                processTag,
                """Process: $process.
                Status: 503 ${HttpStatusUtil.getStatusName(503)}.
                Response: Error connecting to server.""".trimIndent(),
                e
            )
            deferredResult.complete(createErrorResponse("Error connecting to server", returnType))
        } catch (e: Exception) {
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

@Suppress("UNCHECKED_CAST")
fun <T> createErrorResponse(response: String, returnType: String): ResponseApi<T> {
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

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
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline apiFunction: suspend () -> Response<ResponseApi<T>>,
): ResponseApi<T> {
    val deferredResult = CompletableDeferred<ResponseApi<T>>()

    GlobalScope.launch(dispatcher) {
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
                    response.body() ?: ResponseApi(false, "Error receiving response from server", null)
                deferredResult.complete(responseBody as ResponseApi<T>)
            } else {
                when (response.code()) {
                    401 -> {
                        AppLogger.w(
                            processTag,
                            """Process: $process.
                            Status: 401 Unauthorized.
                            Response: Unauthorized access.""".trimIndent()
                        )
                        deferredResult.complete(ResponseApi(false, "Unauthorized", null))
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
                            AppLogger.e(
                                processTag,
                                """Process: $process.
                                Status: 422 ${HttpStatusUtil.getStatusName(422)}.
                                Response: Error parsing error response.""".trimIndent(),
                                e
                            )
                            ResponseApi(false, "Internal error parsing response", null)
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
            deferredResult.complete(ResponseApi(false, "Error connecting to server", null))
        } catch (e: Exception) {
            AppLogger.e(
                processTag,
                """Process: $process.
                Status: Unknown error.
                Response: Internal app error.""".trimIndent(),
                e
            )
            deferredResult.complete(ResponseApi(false, "The app experienced an unexpected internal error", null))
        }
    }

    return runBlocking { deferredResult.await() }
}
package org.example.project.persistence.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.project.logging.AppLogger
import org.example.project.persistence.network.HttpStatusUtil
import org.example.project.persistence.network.ResponseApi
import retrofit2.Response

@OptIn(DelicateCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
fun <T> responseHandler(
    loggingTag: String,
    returnType: String,
    apiFunction: suspend () -> Response<ResponseApi<T>>,
): ResponseApi<T>? {
    var result: ResponseApi<T>? = null
    val deferredResult = CompletableDeferred<ResponseApi<T>?>()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Connect to server
            AppLogger.i(loggingTag, "Attempting connection")
            val response: Response<ResponseApi<T>> = apiFunction()

            if (response.isSuccessful) {
                // Log successful attempt and return body
                AppLogger.i(
                    loggingTag,
                    "Status: ${response.code()} ${HttpStatusUtil.getStatusName(response.code())}," +
                            " Response: ${response.body()?.response!!}"
                )
                result = response.body()
            } else {
                // Register failed attempt and return error body
                val errorBody = response.errorBody()?.string()
                val gson = Gson()
                val type = object : TypeToken<ResponseApi<T>>() {}.type
                val errorResponse: ResponseApi<T> = gson.fromJson(errorBody, type)

                AppLogger.w(
                    loggingTag,
                    "Status: ${response.code()} ${HttpStatusUtil.getStatusName(response.code())}," +
                            " Response: ${errorResponse.response}"
                )
                result = errorResponse
            }
            deferredResult.complete(result)
        } catch (e: java.net.ConnectException) {
            // Network error -> log error and return error response according to necessary type
            AppLogger.e(
                loggingTag,
                "Status: 503 ${HttpStatusUtil.getStatusName(503)}," +
                        " Response: Error connecting to server",
                e
            )
            result = if (returnType == "String") {
                ResponseApi(
                    false,
                    "Error connecting to server",
                    "Error connecting to server"
                ) as ResponseApi<T>
            } else {
                ResponseApi(false, "Error connecting to server", emptyList<T>()) as ResponseApi<T>
            }
            deferredResult.complete(result)
        } catch (e: Exception) {
            // Other exceptions -> log the error and return error response according to necessary type
            AppLogger.e(
                loggingTag,
                "Status: ___, Unknown error",
                e
            )
            result = if (returnType == "String") {
                ResponseApi(false, "Internal app error", "Internal app error") as ResponseApi<T>
            } else {
                ResponseApi(false, "Internal app error", emptyList<T>()) as ResponseApi<T>
            }
            deferredResult.complete(result)
        }
    }

    println(result?.response)
    return runBlocking { deferredResult.await() }
}

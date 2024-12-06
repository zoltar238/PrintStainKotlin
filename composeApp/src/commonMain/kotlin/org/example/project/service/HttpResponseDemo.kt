package org.example.project.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.project.entity.ServerDto

suspend inline fun <reified T> httpGetRequestDemo(url: String): ServerDto<T>? {
    val client = HttpClient(CIO)

    try {
        // Get response from server
        val responseBody = client.get(url).bodyAsText()
        // Transform json response to text
        return Json.decodeFromString(responseBody)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        client.close()
    }
}

@Serializable
data class User(
    val username: String,
    val password: String,
)

suspend inline fun <reified T> httpPostRequestDemo(
    url: String,
    username: String,
    password: String,
): ServerDto<T>? {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ingoreUnknownKeys = true
            })
        }
    }


    try {
        // Get response from server
        val response: HttpResponse = client.get(url) {
            contentType(ContentType.Application.Json)
            setBody(User(username, password))
        }
        // Transform json response to text
        return response.body()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        client.close()
    }
}
package org.example.project.persistence.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.project.persistence.preferences.PreferencesManager
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.net.URI

object ItemsRepoHttpImp : ItemsRepo {
    override fun getAllItems(): String {

        val token = kotlinx.coroutines.runBlocking {
            PreferencesManager.getToken()
        }

        // Create uri
        val uri = URI.create("http://localhost:8080/item/getAllItems")

        // Create client
        val client: HttpHandler = ApacheClient()

        // Create get request
        val request = Request(Method.GET, uri.toString())
            .header("Authorization", "Bearer $token")

        // Return response as json text
        return client.invoke(request).bodyString()
    }

    override fun getAllUserItems(): String {
        TODO("Not yet implemented")
    }
}
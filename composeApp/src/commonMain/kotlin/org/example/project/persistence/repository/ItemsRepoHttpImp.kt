package org.example.project.persistence.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.net.URI

object ItemsRepoHttpImp : ItemsRepo {
    override fun getAllItems(): String {
        // Map user to JSON
        val objectMapper = jacksonObjectMapper().registerKotlinModule()

        // Create uri
        val uri = URI.create("http://localhost:8080/item/getAllItems")

        // Create client
        val client: HttpHandler = ApacheClient()

        // Create get request
        val request = Request(Method.GET, uri.toString())

        // Return response as json text
        return client.invoke(request).bodyString()
    }

    override fun getAllUserItems(): String {
        TODO("Not yet implemented")
    }
}
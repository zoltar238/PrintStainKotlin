package org.example.project.service

import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.net.URI

fun checkHealth() {
    val uri = URI.create("http://localhost:8080/healthcheck")

    val client: HttpHandler = ApacheClient()


    val request = Request(Method.GET, uri.toString())

    val response = client.invoke(request)

    println(response.bodyString())
}



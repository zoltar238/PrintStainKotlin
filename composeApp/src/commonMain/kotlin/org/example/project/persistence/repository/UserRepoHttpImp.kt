package org.example.project.persistence.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.project.model.UserDto
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.net.URI

object UserRepoHttpImp : UserRepo {

    @Throws
    override fun registerUser(userDto: UserDto): String {

        // Map user to JSON
        val objectMapper = jacksonObjectMapper().registerKotlinModule()
        val userJson = objectMapper.writeValueAsString(userDto)

        // Create uri
        val uri = URI.create("http://localhost:8080/person/register")

        // Create client
        val client: HttpHandler = ApacheClient()

        // Create post request with user transformer to json as body
        val request = Request(Method.POST, uri.toString())
            .header("Content-Type", "application/json")
            .body(userJson)

        // Return response as json text
        return client.invoke(request).bodyString()
    }

    override fun loginUser(userDto: UserDto): String {

        // Map user to JSON
        val objectMapper = jacksonObjectMapper().registerKotlinModule()
        val userJson = objectMapper.writeValueAsString(userDto)

        // Create uri
        val uri = URI.create("http://localhost:8080/person/login")

        // Create client
        val client: HttpHandler = ApacheClient()

        // Create post request with user transformer to json as body
        val request = Request(Method.POST, uri.toString())
            .header("Content-Type", "application/json")
            .body(userJson)

        // Return response as json text
        return client.invoke(request).bodyString()
    }
}
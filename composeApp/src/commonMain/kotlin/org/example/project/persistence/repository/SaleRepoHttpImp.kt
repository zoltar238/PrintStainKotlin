package org.example.project.persistence.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.project.model.SaleDto
import org.example.project.persistence.network.ApiClient
import org.example.project.persistence.preferences.PreferencesManager
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.net.URI

object SaleRepoHttpImp: SaleRepo {
    override fun createNewSale(saleDto: SaleDto): String {

        // Obtain access token
        val token = kotlinx.coroutines.runBlocking {
            PreferencesManager.getToken()
        }

        // Map sale to JSON
        val objectMapper = jacksonObjectMapper().registerKotlinModule()
        val saleJson = objectMapper.writeValueAsString(saleDto)

        // Create uri
        val uri = URI.create("http://localhost:8080/sale/newSale")

        // Create client
        val client: HttpHandler = ApacheClient()

        // Create post request with user transformer to json as body
        val request = Request(Method.POST, uri.toString())
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .body(saleJson)

        // Return response as json text
        return client.invoke(request).bodyString()
    }

    override fun deleteSale(saleDto: SaleDto): String {
        TODO("Not yet implemented")
    }

    override fun getAllSales(): String {
        return kotlinx.coroutines.runBlocking {
            val token = PreferencesManager.getToken()
            ApiClient.saleApi.getAllSales("Bearer $token")
        }
    }
}
package org.example.project.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.logging.AppLogger
import org.example.project.model.AllSalesDto
import org.example.project.model.SaleDto
import org.example.project.persistence.repository.SaleRepoHttpImp

fun createNewSale(saleDto: SaleDto): Pair<Boolean, String> {
    // Create JSON Mapper
    val objectMapper = jacksonObjectMapper()

    // Serialize response object to json
    val jsonResponse = SaleRepoHttpImp.createNewSale(saleDto)
    val jsonString = objectMapper.writeValueAsString(jsonResponse)

    try {
        // Parse JSON
        val rootNode: JsonNode = objectMapper.readTree(jsonString)
        val success = rootNode.get("success")?.asBoolean() ?: false
        val response = rootNode.get("response")?.asText() ?: "Unknown response"
        val data = rootNode.get("data")?.asText() ?: "Unknown response"

        AppLogger.i("NewSaleCreation", response)

        // Return response
        return success to data

    } catch (e: Exception) {
        AppLogger.e("ServerConnection", "Could not connect to server", e)
        return false to "Connection error"
    }
}

fun findAllSales(): Pair<Boolean, List<AllSalesDto>>{
    // Create JSON Mapper
    val objectMapper = jacksonObjectMapper()

    // Serialize response object to json
    val jsonResponse = SaleRepoHttpImp.findAllSales()
    val jsonString = objectMapper.writeValueAsString(jsonResponse)

    try {
        // Parse JSON
        val rootNode: JsonNode = objectMapper.readTree(jsonString)
        val success = rootNode.get("success")?.asBoolean() ?: false
        val response = rootNode.get("response")?.asText() ?: "Unknown response"
        val data = rootNode.get("data")

        AppLogger.i("FindAllSaleData", response)

        println(data.toString())

        // Map JSON response to object
        val allSalesDtoList: List<AllSalesDto> = if (data != null && data.isArray) {
            objectMapper.readValue(data.toString(), object : TypeReference<List<AllSalesDto>>() {})
        } else {
            emptyList()
        }

        // Log
        AppLogger.i("FindAllSaleData", response)

        // Return response
        return success to allSalesDtoList

    } catch (e: Exception) {
        AppLogger.e("ServerConnection", "Could not connect to server", e)
        return false to emptyList()
    }
}
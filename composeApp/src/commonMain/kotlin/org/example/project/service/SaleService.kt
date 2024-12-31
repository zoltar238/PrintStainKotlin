package org.example.project.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.logging.AppLogger
import org.example.project.model.SaleDto
import org.example.project.persistence.repository.SaleRepoHttpImp

fun createNewSale(saleDto: SaleDto): Pair<Boolean, String> {
    // Create mapper and read JSON
    val objectMapper = jacksonObjectMapper()
    val rootNode: JsonNode =
        objectMapper.readTree(SaleRepoHttpImp.createNewSale(saleDto))

    try {
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
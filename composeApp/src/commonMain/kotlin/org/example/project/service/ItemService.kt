package org.example.project.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.controller.decodeBase64ToBitmap
import org.example.project.logging.AppLogger
import org.example.project.model.ItemDto
import org.example.project.persistence.repository.ItemsRepoHttpImp

// Function reserved for admin privileges
fun getAllItems(): Pair<Boolean, List<ItemDto>> {
    // Create mapper and read JSON
    val objectMapper = jacksonObjectMapper()
    val rootNode: JsonNode = objectMapper.readTree(ItemsRepoHttpImp.getAllItems())

    // Process received information
    try {
        val success = rootNode.get("success")?.asBoolean() ?: false
        val response = rootNode.get("response")?.asText() ?: "Unknown response"
        val data = rootNode.get("data")

        // Log response received from server
        AppLogger.i("GetAllImages", response)

        // Map JSON response to object
        val itemDtoLists: List<ItemDto> = if (data != null && data.isArray) {
            objectMapper.readValue(data.toString(), object : TypeReference<List<ItemDto>>() {})
        } else {
            emptyList()
        }

        // Log amount of items received
        AppLogger.i("Items received: ", itemDtoLists.size.toString())

        //transform base64 images to bitmap
        if (itemDtoLists.isNotEmpty()) {
            itemDtoLists.forEach{item ->
                item.base64Images?.forEach{ base64image ->
                    item.bitmapImages.add(decodeBase64ToBitmap(base64image))
                }
            }
        }

        // Return data
        return success to itemDtoLists
    } catch (e: Exception) {
        AppLogger.e("ServerConnection", "Could not connect to server", e)
        return false to emptyList()
    }
}
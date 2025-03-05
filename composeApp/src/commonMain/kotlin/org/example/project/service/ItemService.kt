package org.example.project.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.util.decodeBase64ToBitmap
import org.example.project.logging.AppLogger
import org.example.project.model.ItemDto
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.repository.ItemsRepoHttpImp

// Function reserved for admin privileges
fun getAllItems(): ResponseApi<List<ItemDto>> {
    // Receive all items from server
    val serverResponse = ItemsRepoHttpImp.getAllItems()
    // Decode base64 images to bitmap
    if (serverResponse?.data?.isNotEmpty() == true) {
        serverResponse.data.forEach { item ->
        item.base64Images?.forEach { image ->
            item.bitmapImages.add(decodeBase64ToBitmap(image))
        } }
    }
    return serverResponse!!
}
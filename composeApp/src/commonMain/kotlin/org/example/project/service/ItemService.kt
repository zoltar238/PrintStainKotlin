package org.example.project.service

import org.example.project.model.dto.ItemDto
import kotlinx.coroutines.runBlocking
import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.logging.LoggingTags
import org.example.project.model.entity.Item
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.repository.responseHandler
import org.example.project.service.mapping.Mapper.mapItemDtoToRealm
import org.example.project.util.decodeBase64ToBitmap

// Function reserved for admin privileges
fun getAllItems(): MutableList<Item> {
    // Get access token
    val token = runBlocking {
        PreferencesManager.getToken()
    }

    // Get all items from server
    val serverResponse = responseHandler(
        "Get all items from server",
        LoggingTags.ItemsGetAll.name,
        "List"
    ) { ClientController.itemController.getAllItems("Bearer $token") }

    // Map images to realm objects
    var items: MutableList<Item> = mutableListOf()

    if (serverResponse.data.isNotEmpty()) {
        serverResponse.data.forEach{ item -> 
            items.add(mapItemDtoToRealm(item))
        }
    }
    
//    // Decode base64 images to bitmap
//    if (serverResponse.data.isNotEmpty()) {
//        serverResponse.data.forEach { item ->
//            item.base64Images?.forEach { image ->
//                item.bitmapImages.add(decodeBase64ToBitmap(image))
//            }
//        }
//    }
    return items
}
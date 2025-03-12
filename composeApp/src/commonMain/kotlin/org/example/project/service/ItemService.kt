package org.example.project.service

import kotlinx.coroutines.runBlocking
import org.example.project.controller.ClientController
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.ItemDto
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.repository.responseHandler

// Function reserved for admin privileges
fun getAllItems(): MutableList<ItemDto> {
    // Get access token
    val token = runBlocking {
        PreferencesManager.getToken()
    }

    // Get all items from server
    val serverResponse = responseHandler(
        "Get all items from server",
        ProcessTags.ItemsGetAll.name,
        "List"
    ) { ClientController.itemController.getAllItems("Bearer $token") }

    // Map images to realm objects
    val items: MutableList<ItemDto> = mutableListOf()

    return items
}
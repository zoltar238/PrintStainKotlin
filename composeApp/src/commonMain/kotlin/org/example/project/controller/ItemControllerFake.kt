package org.example.project.controller

import org.example.project.model.ItemDto
import org.example.project.service.getAllItems

object ItemControllerFake {
    var items: List<ItemDto> = emptyList()
    private var itemsSuccess: Boolean = false
    lateinit var itemStatus: String

    // Constructor
    fun getItems() {
        // Obtain all items
        val serverResponse = getAllItems()
        this.itemsSuccess = serverResponse.success
        this.items = serverResponse.data
        itemStatus = setItemStatus()
    }

    private fun setItemStatus(): String {
        return if (itemsSuccess && items.isNotEmpty()) {
            "OK"
        } else if (!itemsSuccess) {
            "The server experienced an error loading the models"
        } else if (itemsSuccess && items.isEmpty()) {
            "Internal error while processing models"
        } else {
            "Unexpected error processing models"
        }
    }

    fun getItemById(itemId: Long): ItemDto? {
        items.forEach { item ->
            if (item.itemId == itemId) {
                return item
            }
        }
        return null;
    }
}

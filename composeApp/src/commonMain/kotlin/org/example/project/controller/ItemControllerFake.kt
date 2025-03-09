package org.example.project.controller

import org.example.project.model.dto.ItemDto
import org.example.project.model.entity.Item
import org.example.project.service.getAllItems

object ItemControllerFake {
    var items: MutableList<Item> = mutableListOf()
    private var itemsSuccess: Boolean = false
    lateinit var itemStatus: String

    // Constructor
    fun getItems() {
        // Obtain all items
        val items = getAllItems()
        this.itemsSuccess = true
        this.items = items
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

    fun getItemById(itemId: Long): Item? {
        items.forEach { item ->
            if (item.id == itemId) {
                return item
            }
        }
        return null;
    }
}

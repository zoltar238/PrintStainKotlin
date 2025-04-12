package org.example.project.service

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.first
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.controller.responseHandler
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.ImageDto
import org.example.project.model.dto.ItemDto
import org.example.project.model.dto.ItemWithRelations
import org.example.project.persistence.database.*
import org.example.project.persistence.preferences.PreferencesDaoImpl
import org.example.project.util.encodeBitmapToBase64

class ItemService(database: PrintStainDatabase) {
    private val itemDao: ItemDao = ItemDaoImpl(database)
    private val imageDao: ImageDao = ImageDaoImpl(database)
    private val personDao: PersonDao = PersonDaoImpl(database)

    suspend fun fetchAllItemsFromServer(): ResponseApi<List<ItemDto>> {
        val token = PreferencesDaoImpl.getToken()
        return responseHandler(
            "Get all items from server",
            ProcessTags.ItemsGetAll.name,
        ) { ClientController.itemController.getAllItems("Bearer $token") }
    }

    suspend fun createItemOnServer(itemDto: ItemDto): ResponseApi<ItemDto> {
        val token = PreferencesDaoImpl.getToken()
        return responseHandler(
            "Create new item",
            ProcessTags.CreateNewItem.name,
        ) { ClientController.itemController.postItem("Bearer $token", itemDto) }
    }

    suspend fun updateItemOnServer(itemDto: ItemDto): ResponseApi<ItemDto> {
        val token = PreferencesDaoImpl.getToken()
        return responseHandler(
            "Update item",
            ProcessTags.UpdateItem.name,
        ) { ClientController.itemController.updateItem("Bearer $token", itemDto) }
    }

    suspend fun deleteItemsOnServer(items: List<ItemDto>): ResponseApi<String> {
        val token = PreferencesDaoImpl.getToken()
        return responseHandler(
            "Delete items from server",
            ProcessTags.DeleteItems.name,
        ) { ClientController.itemController.deleteItems("Bearer $token", items) }
    }

    suspend fun processItemsToLocalDB(items: List<ItemDto>) {
        items.forEach { item ->
            itemDao.insertItem(
                itemId = item.itemId!!,
                name = item.name,
                description = item.description,
                postDate = item.postDate.toString(),
                timesUploaded = item.timesUploaded,
                personId = item.person?.personId
            )
            item.images?.forEach { image ->
                imageDao.insertImage(
                    imageId = image.imageId!!,
                    base64Image = image.base64Image!!,
                    item_id = item.itemId
                )
            }
            item.person?.let {
                personDao.insertPerson(
                    it.personId!!,
                    it.name!!
                )
            }
        }
    }

    suspend fun getAllLocalItems(): List<ItemWithRelations> {
        return itemDao.getAllItemsWithRelation().first()
    }

    suspend fun getItemById(id: Long): ItemWithRelations? {
        val allItems = getAllLocalItems()
        return allItems.firstOrNull { it.item.itemId == id }
    }

    suspend fun deleteLocalItems(items: List<ItemWithRelations>) {
        items.forEach { item ->
            itemDao.deleteItem(item.item.itemId)
        }
    }

    suspend fun createItemDto(name: String, description: String, images: List<ImageBitmap>): ItemDto {
        return ItemDto(
            name = name,
            description = description,
            images = images.filter { it.width > 1 && it.height > 1 }
                .map { ImageDto(base64Image = encodeBitmapToBase64(it)) }
        )
    }

    suspend fun deleteImagesForItem(itemId: Long) {
        imageDao.deleteImagesById(itemId)
    }

    suspend fun checkItemsHaveSales(items: List<ItemWithRelations>): ItemWithRelations? {
        return items.firstOrNull { it.sales?.isNotEmpty() == true }
    }
}

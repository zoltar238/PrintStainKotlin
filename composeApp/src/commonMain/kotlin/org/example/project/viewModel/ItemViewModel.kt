package org.example.project.viewModel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.ImageDto
import org.example.project.model.dto.ItemDto
import org.example.project.model.dto.ItemWithRelations
import org.example.project.model.dto.PersonDto
import org.example.project.persistence.database.*
import org.example.project.persistence.preferences.PreferencesDaoImpl
import org.example.project.util.encodeBitmapToBase64


data class ItemUiState(
    val items: List<ItemWithRelations> = emptyList(),
    val selectedItem: ItemWithRelations? = null,
    val isLoading: Boolean = false,
    val messageEvent: MessageEvent? = null,
    val success: Boolean = true,
)

class ItemViewModel(
    database: PrintStainDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val itemDao: ItemDao = ItemDaoImpl(database)
    private val imageDao: ImageDao = ImageDaoImpl(database)
    private val personDao: PersonDao = PersonDaoImpl(database)

    private val _itemUiState = MutableStateFlow(ItemUiState(isLoading = true))
    val itemUiState: StateFlow<ItemUiState> = _itemUiState.asStateFlow()

    fun consumeMessage() {
        _itemUiState.update { currentState ->
            currentState.copy(
                messageEvent = currentState.messageEvent?.consume()
            )
        }
    }

    fun getItemById(id: Long) {
        viewModelScope.launch(dispatcher) {
            try {
                _itemUiState.update { it.copy(isLoading = true) }

                // Select item from the list
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        success = true,
                        selectedItem = _itemUiState.value.items.first { individualItem ->
                            individualItem.item.itemId == id
                        })
                }
            } catch (e: Exception) {
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        messageEvent = MessageEvent("Error loading item"),
                        selectedItem = null
                    )
                }
            }
        }
    }

    fun getAllItems() {
        viewModelScope.launch(dispatcher) {
            try {
                // Update state to loading
                _itemUiState.update { it.copy(isLoading = true) }

                // Obtain token
                val token = PreferencesDaoImpl.getToken()

                println("Token: $token")

                // Get items from server
                val serverResponse = responseHandler(
                    "Get all items from server",
                    ProcessTags.ItemsGetAll.name,
                    "List"
                ) { ClientController.itemController.getAllItems("Bearer $token") }

                if (!serverResponse.success) {
                    // Update state with the newly received items
                    _itemUiState.update {
                        it.copy(
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response),
                            success = false
                        )
                    }
                } else {
                    insertItems(serverResponse.data, serverResponse.response)
                }
            } catch (e: Exception) {
                // Handle posible SQL exceptions
                AppLogger.e(
                    "Get all items from server",
                    """
                        Process: ${ProcessTags.GetAllItems.name}.
                        Status: Internal sql error loading all items.
                    """.trimIndent(),
                    e
                )
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}"),
                        success = false
                    )
                }
            }
        }
    }

    private suspend fun insertItems(
        data: List<ItemDto>,
        response: String?,
    ) {
        // Save items in the local database
        data.forEach { item ->
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
            personDao.insertPerson(
                item.person?.personId!!,
                item.person.name!!
            )
        }

        // Get updated items from the database
        val localItems = itemDao.getAllItemsWithRelation().first()

        // Update state with the newly received items
        _itemUiState.update {
            it.copy(
                items = localItems,
                isLoading = false,
                messageEvent = MessageEvent(response ?: "Items loaded successfully"),
                success = true
            )
        }
    }

    fun createItem(name: String, description: String, images: List<ImageBitmap>) {
        viewModelScope.launch(dispatcher) {
            try {
                // Update state to loading
                _itemUiState.update { it.copy(isLoading = true) }

                // Create ItemDto
                val itemDto = ItemDto(
                    name = name,
                    description = description,
                    images = images.filter { it.width > 1 }.map {
                        ImageDto(base64Image = encodeBitmapToBase64(it))
                    }
                )

                // Get token
                val token = PreferencesDaoImpl.getToken()

                // Post item on server
                val serverResponse = responseHandler(
                    "Get all items from server",
                    ProcessTags.ItemsGetAll.name,
                    "List"
                ) {
                    ClientController.itemController.postItem(
                        "Bearer $token",
                        itemDto = itemDto
                    )
                }

                if (!serverResponse.success) {
                    // Update state with the newly received items
                    _itemUiState.update {
                        it.copy(
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response),
                            success = false
                        )
                    }
                } else {
                    // Save item in the local database
                    insertItems(
                        data = listOf(serverResponse.data),
                        response = serverResponse.response
                    )
                }

            } catch (e: Exception) {
                // Handle posible exceptions
                AppLogger.e(
                    "Create new item",
                    """
                        Process: ${ProcessTags.CreateNewItem.name}.
                        Status: Internal sql error loading all items.
                    """.trimIndent(),
                    e
                )
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}"),
                        success = false
                    )
                }
            }
        }
    }

    fun deleteItem(items: List<ItemWithRelations>) {
        viewModelScope.launch(dispatcher) {
            try {
                _itemUiState.update { it.copy(isLoading = true) }

                val itemWithSales = items.firstOrNull { it.sales?.isNotEmpty() == true }

                if (itemWithSales != null) {
                    _itemUiState.update {
                        it.copy(
                            isLoading = false,
                            messageEvent = MessageEvent("Error: Item \"${itemWithSales.item.name}\" has related sales and cannot be deleted, please delete the sales first or archive this model."),
                            success = false
                        )
                    }
                } else {
                    val token = PreferencesDaoImpl.getToken()

                    // Delete items from server
                    val serverResponse = responseHandler(
                        "Delete items from server",
                        ProcessTags.DeleteItems.name,
                        "List"
                    ) {
                        ClientController.itemController.deleteItems(
                            "Bearer $token",
                            items.map { ItemDto(it.item.itemId) }
                        )
                    }

                    if (!serverResponse.success) {
                        // Update state with the newly received items
                        _itemUiState.update {
                            it.copy(
                                isLoading = false,
                                messageEvent = MessageEvent(serverResponse.response),
                                success = false
                            )
                        }
                    } else {
                        // Delete items from the local database
                        items.forEach { item ->
                            itemDao.deleteItem(item.item.itemId)
                        }

                        // Get updated items from the database
                        val localItems = itemDao.getAllItemsWithRelation().first()

                        // Update state with the newly received items
                        _itemUiState.update {
                            it.copy(
                                items = localItems,
                                isLoading = false,
                                messageEvent = MessageEvent(serverResponse.response),
                                success = true
                            )
                        }
                    }
                }
                // Get token
            } catch (e: Exception) {
                // Handle posible exceptions
                AppLogger.e(
                    "Delete items",
                    """
                        Process: ${ProcessTags.DeleteItems.name}.
                        Status: Internal sql error loading all items.
                    """.trimIndent(),
                    e
                )
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}"),
                        success = false
                    )
                }
            }
        }
    }

    fun updateItems() {
        viewModelScope.launch(dispatcher) {
            try {
                AppLogger.i(
                    "Update items locally",
                    """
                    Process: Local item update.
                    Status: Updating local items.
                """.trimIndent(),
                )

                // Indicate the operation has started
                _itemUiState.update { it.copy(isLoading = true) }

                // Get updated items directly from the local database
                val localItems = itemDao.getAllItemsWithRelation().first()

                // Update state with retrieved items
                _itemUiState.update {
                    it.copy(
                        items = localItems,
                        isLoading = false,
                        messageEvent = MessageEvent("Models have been updated"),
                        success = true
                    )
                }
            } catch (e: Exception) {
                // Handle possible exceptions
                AppLogger.e(
                    "Update items locally",
                    """
                    Process: Local update.
                    Status: Error retrieving items from local database.
                """.trimIndent(),
                    e
                )
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        messageEvent = MessageEvent("Error updating items: ${e.localizedMessage}"),
                        success = false
                    )
                }
            }
        }
    }

    fun modifyItem(name: String, description: String, images: List<ImageBitmap>) {
        viewModelScope.launch(dispatcher) {
            try {
                AppLogger.i(
                    "Update items locally",
                    """
                    Process: Local item update.
                    Status: Updating local items.
                """.trimIndent(),
                )
                // Indicate the operation has started
                _itemUiState.update { it.copy(isLoading = true) }

                // Get token
                val token = PreferencesDaoImpl.getToken()

                // Create ItemDto
                val itemDto = ItemDto(
                    itemId = itemUiState.value.selectedItem?.item?.itemId,
                    name = name,
                    description = description,
                    images = images.filter { (it.width > 1 && it.height > 1) }.map { image ->
                            ImageDto(base64Image = encodeBitmapToBase64(image))
                    },
                    person = PersonDto(
                        personId = itemUiState.value.selectedItem?.person?.personId,
                        name = itemUiState.value.selectedItem?.person?.name
                    )
                )

                // Update items on server
                val serverResponse = responseHandler(
                    "Update model",
                    ProcessTags.ItemsGetAll.name,
                    "List"
                ) { ClientController.itemController.updateItem("Bearer $token", itemDto) }

                if (!serverResponse.success) {
                    // Update state with the newly received items
                    _itemUiState.update {
                        it.copy(
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response),
                            success = false
                        )
                    }
                } else {
                    // Remove previous images
                    _itemUiState.value.selectedItem?.item?.let { imageDao.deleteImagesById(it.itemId) }
                    // Save item in the local database
                    insertItems(listOf(serverResponse.data), serverResponse.response)
                    // Get updated items directly from the local database
                    val localItems = itemDao.getAllItemsWithRelation().first()

                    // Update state with retrieved items
                    _itemUiState.update {
                        it.copy(
                            items = localItems,
                            isLoading = false,
                            messageEvent = MessageEvent("Model has been updated"),
                            success = true
                        )
                    }
                }

            } catch (e: Exception) {
                // Handle possible exceptions
                AppLogger.e(
                    "Update items locally",
                    """
                    Process: Local update.
                    Status: Error retrieving items from local database.
                """.trimIndent(),
                    e
                )
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        messageEvent = MessageEvent("Error updating items: ${e.localizedMessage}"),
                        success = false
                    )
                }
            }
        }
    }
}
package org.example.project.viewModel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.model.MessageEvent
import org.example.project.model.dto.ImageDto
import org.example.project.model.dto.ItemDto
import org.example.project.model.dto.ItemWithRelations
import org.example.project.model.dto.PersonDto
import org.example.project.service.ItemService
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

    private val itemService = ItemService(database)

    private val _itemUiState = MutableStateFlow(ItemUiState(isLoading = true))
    val itemUiState: StateFlow<ItemUiState> = _itemUiState.asStateFlow()

    fun consumeMessage() {
        _itemUiState.update { currentState ->
            currentState.copy(messageEvent = currentState.messageEvent?.consume())
        }
    }

    fun getItemById(id: Long) {
        viewModelScope.launch(dispatcher) {
            try {
                _itemUiState.update { it.copy(isLoading = true) }
                val item = itemService.getItemById(id)
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        success = item != null,
                        selectedItem = item,
                        messageEvent = if (item == null) MessageEvent("Item not found") else null
                    )
                }
            } catch (e: Exception) {
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        messageEvent = MessageEvent("Error loading item: ${e.localizedMessage}"),
                        selectedItem = null
                    )
                }
            }
        }
    }

    fun getAllItems() {
        viewModelScope.launch(dispatcher) {
            try {
                _itemUiState.update { it.copy(isLoading = true) }

                val serverResponse = itemService.fetchAllItemsFromServer()

                if (!serverResponse.success) {
                    _itemUiState.update {
                        it.copy(
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response!!),
                            success = false
                        )
                    }
                } else {
                    serverResponse.data?.let {
                        itemService.processItemsToLocalDB(it)
                    }

                    val localItems = itemService.getAllLocalItems()

                    _itemUiState.update {
                        it.copy(
                            items = localItems,
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response!!),
                            success = true
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e(
                    "Get all items from server",
                    "Process: ${ProcessTags.GetAllItems.name}. Status: Internal error loading all items.",
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

    fun createItem(name: String, description: String, images: List<ImageBitmap>) {
        viewModelScope.launch(dispatcher) {
            try {
                _itemUiState.update { it.copy(isLoading = true) }

                val itemDto = itemService.createItemDto(name, description, images)

                val serverResponse = itemService.createItemOnServer(itemDto)

                if (!serverResponse.success) {
                    _itemUiState.update {
                        it.copy(
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response!!),
                            success = false
                        )
                    }
                } else {
                    serverResponse.data?.let {
                        itemService.processItemsToLocalDB(listOf(it))
                    }

                    val localItems = itemService.getAllLocalItems()

                    _itemUiState.update {
                        it.copy(
                            items = localItems,
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response!!),
                            success = true
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e(
                    "Create new item",
                    "Process: ${ProcessTags.CreateNewItem.name}. Status: Internal error creating new item.",
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
                    val serverResponse = itemService.deleteItemsOnServer(items.map { ItemDto(it.item.itemId) })

                    if (serverResponse.success) {
                        itemService.deleteLocalItems(items)
                    }

                    _itemUiState.update {
                        it.copy(
                            items = if (serverResponse.success) itemService.getAllLocalItems() else _itemUiState.value.items,
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response!!),
                            success = serverResponse.success
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e(
                    "Delete items",
                    "Process: ${ProcessTags.DeleteItems.name}. Status: Internal error deleting items.",
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
                val localItems = itemService.getAllLocalItems()

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
                _itemUiState.update { it.copy(isLoading = true) }

                // Obtener el item seleccionado
                val selectedItem = itemUiState.value.selectedItem
                if (selectedItem == null) {
                    _itemUiState.update {
                        it.copy(
                            isLoading = false,
                            messageEvent = MessageEvent("No item selected for modification"),
                            success = false
                        )
                    }
                    return@launch
                }

                val serverResponse = itemService.updateItemOnServer(
                    ItemDto(
                        itemId = selectedItem.item.itemId,
                        name = name,
                        description = description,
                        images = images.filter { (it.width > 1 && it.height > 1) }.map { image ->
                            ImageDto(base64Image = encodeBitmapToBase64(image))
                        },
                        person = PersonDto(
                            personId = selectedItem.person?.personId,
                            name = selectedItem.person?.name
                        )
                    )
                )

                if (!serverResponse.success) {
                    _itemUiState.update {
                        it.copy(
                            isLoading = false,
                            messageEvent = MessageEvent(serverResponse.response!!),
                            success = false
                        )
                    }
                } else {
                    // Pasos críticos que faltaban para actualizar correctamente:
                    // 1. Eliminar imágenes antiguas
                    selectedItem.item.itemId.let { itemService.deleteImagesForItem(it) }
                    
                    // 2. Procesar el nuevo item con sus relaciones en la DB local
                    serverResponse.data?.let { itemService.processItemsToLocalDB(listOf(it)) }
                    
                    // 3. Obtener los items actualizados
                    val localItems = itemService.getAllLocalItems()

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
                AppLogger.e(
                    "Modify item",
                    "Process: ${ProcessTags.UpdateItem.name}. Status: Internal error updating item.",
                    e
                )
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        messageEvent = MessageEvent("Error updating item: ${e.localizedMessage}"),
                        success = false
                    )
                }
            }
        }
    }
}
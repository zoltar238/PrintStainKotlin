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
import kotlinx.serialization.json.Json
import org.example.project.PrintStainDatabase
import org.example.project.model.MessageEvent
import org.example.project.model.dto.*
import org.example.project.service.ItemService
import org.example.project.util.encodeBitmapToBase64

data class ItemUiState(
    val items: List<ItemWithRelations> = emptyList(),
    val selectedItem: ItemWithRelations? = null,
    val selectedItemFiles: List<FileDto>? = if (selectedItem?.item?.fileStructure == null) emptyList() else Json.decodeFromString<List<FileDto>>(
        selectedItem.item.fileStructure
    ),
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
            _itemUiState.update { it.copy(isLoading = true) }
            val item = itemService.getItemById(id)
            _itemUiState.update {
                it.copy(
                    isLoading = false,
                    success = item != null,
                    selectedItem = item,
                    selectedItemFiles = if (item?.item?.fileStructure.isNullOrEmpty()) emptyList() else Json.decodeFromString<List<FileDto>>(
                        item.item.fileStructure.replace("\\", "").replaceFirst("\"", "").dropLast(1)
                    ),
                    messageEvent = if (item == null) MessageEvent("Item not found") else null
                )
            }
        }
    }

    fun getAllItems() {
        viewModelScope.launch(dispatcher) {
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
        }
    }

    fun createItem(name: String, description: String, images: List<ImageBitmap>) {
        viewModelScope.launch(dispatcher) {
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
        }
    }

    fun deleteItem(items: List<ItemWithRelations>) {
        viewModelScope.launch(dispatcher) {
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
        }
    }

    fun updateItems() {
        viewModelScope.launch(dispatcher) {

            // Indicate the operation has started
            _itemUiState.update { it.copy(isLoading = true) }

            // Get updated items directly from the local database
            val localItems = itemService.getAllLocalItems()

            // Update state with retrieved items
            _itemUiState.update {
                it.copy(
                    items = localItems,
                    isLoading = false,
                    success = true
                )
            }
        }
    }

    fun modifyItem(name: String, description: String, images: List<ImageBitmap>) {
        viewModelScope.launch(dispatcher) {
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
        }
    }

    fun deleteAllItemFiles() {
        viewModelScope.launch(dispatcher) {
            _itemUiState.update {
                it.copy(
                    isLoading = false,
                    success = true,
                    selectedItemFiles = emptyList()
                )
            }
        }
    }

    fun deleteItemFile(name: String) {
        viewModelScope.launch(dispatcher) {
            _itemUiState.update {
                it.copy(
                    isLoading = false,
                    success = true,
                    selectedItemFiles = itemUiState.value.selectedItemFiles?.filter { file -> file.fileName != name }
                )
            }
        }
    }

    fun updateItemFiles() {
        viewModelScope.launch(dispatcher) {
            val currentFiles = itemUiState.value.selectedItemFiles ?: emptyList()
            val updatedFiles =
                itemService.updateItemFiles(files = currentFiles.toMutableList()) // Si acepta List<FileDto>


            _itemUiState.update {
                it.copy(
                    selectedItemFiles = updatedFiles, // Ya debería ser una nueva instancia
                    isLoading = false,
                    success = true
                )
            }
        }
    }

    fun downloadItemFiles() {
        viewModelScope.launch(dispatcher) {
            _itemUiState.update { it.copy(isLoading = true) }
            val response = itemService.downloadFiles(
                itemId = itemUiState.value.selectedItem!!.item.itemId,
                itemName = itemUiState.value.selectedItem!!.item.name!!
            )

            _itemUiState.update {
                it.copy(
                    isLoading = false,
                    success = response.success,
                    messageEvent = MessageEvent(response.response),
                )
            }
        }
    }

    fun deleteItemFiles() {
        viewModelScope.launch(dispatcher) {
            _itemUiState.update { it.copy(isLoading = true) }
            val serverResponse = itemService.deleteFiles(
                itemId = itemUiState.value.selectedItem!!.item.itemId,
            )

            if (!serverResponse.success) {
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        messageEvent = MessageEvent(serverResponse.response!!),
                        success = false,
                    )
                }
            } else {
                updateItems()
                _itemUiState.update {
                    it.copy(
                        isLoading = false,
                        messageEvent = MessageEvent(serverResponse.response!!),
                        selectedItemFiles = emptyList(),
                        success = true
                    )
                }
            }
    }
}

fun uploadItemFiles() {
    viewModelScope.launch(dispatcher) {
        _itemUiState.update { it.copy(isLoading = true) }
        val serverResponse = itemService.uploadFiles(
            files = itemUiState.value.selectedItemFiles!!,
            itemId = itemUiState.value.selectedItem!!.item.itemId,
            zipName = itemUiState.value.selectedItem?.item?.name!!
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
            _itemUiState.update {
                itemService.updateFileStructure(
                    itemId = itemUiState.value.selectedItem!!.item.itemId,
                    files = itemUiState.value.selectedItemFiles!!
                )
                updateItems()
                it.copy(
                    isLoading = false,
                    messageEvent = MessageEvent("Files uploaded successfully"),
                    success = true
                )
            }
        }
}
}

fun previewFile(path: String) {
    viewModelScope.launch(dispatcher) {
        _itemUiState.update { it.copy(isLoading = true) }

        val result = itemService.previewFile(path)

        _itemUiState.update {
            it.copy(
                isLoading = false,
                messageEvent = MessageEvent(result.response),
                success = result.success
            )
        }

    }
}
}
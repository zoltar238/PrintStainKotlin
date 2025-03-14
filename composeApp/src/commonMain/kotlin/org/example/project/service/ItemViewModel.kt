package org.example.project.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.logging.ServiceTags
import org.example.project.model.dto.ItemWithRelations
import org.example.project.persistence.database.DriverFactory
import org.example.project.persistence.database.ItemDao
import org.example.project.persistence.database.ItemDaoImpl
import org.example.project.persistence.preferences.PreferencesManager


data class ItemUiState(
    val items: List<ItemWithRelations> = emptyList(),
    val selectedItem: ItemWithRelations? = null,
    val isLoading: Boolean = false,
    val response: String? = null,
    val success: Boolean = true,
)

class ItemViewModel() : ViewModel() {

    private val db: DriverFactory = DriverFactory()
    private val database = PrintStainDatabase.invoke(driver = db.createDriver())
    private val itemDao: ItemDao = ItemDaoImpl(database)

    private val _uiState = MutableStateFlow(ItemUiState(isLoading = true))
    val uiState: StateFlow<ItemUiState> = _uiState.asStateFlow()

    // Auxiliary view models
    private val imageViewModel = ImageViewModel()
    private val personViewModel = PersonViewModel()

    init {
        getAllItems()
    }

    fun getItemById(id: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Select item from the list
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        response = "Item selected successfully",
                        success = true,
                        selectedItem = _uiState.value.items.first { individualItem ->
                            individualItem.item.itemId == id
                        })
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        response = "Internal error loading this item",
                        selectedItem = null
                    )
                }
            }
        }
    }

    fun getAllItems() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Update state to loading
                _uiState.update { it.copy(isLoading = true, response = null) }

                // Obtain token
                val token = PreferencesManager.getToken()

                // Get items from server
                val serverResponse = responseHandler(
                    "Get all items from server",
                    ProcessTags.ItemsGetAll.name,
                    "List"
                ) { ClientController.itemController.getAllItems("Bearer $token") }

                if (!serverResponse.success) {
                    // Update state with the newly received items
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            response = serverResponse.response,
                            success = false
                        )
                    }
                } else {
                    // Save items in the local database
                    serverResponse.data.forEach { item ->
                        itemDao.insertItem(
                            itemId = item.itemId!!,
                            name = item.name,
                            description = item.description,
                            postDate = item.postDate.toString(),
                            startDate = item.startDate.toString(),
                            finishDate = item.finishDate.toString(),
                            shipDate = item.shipDate.toString(),
                            timesUploaded = item.timesUploaded,
                            person_id = item.person?.personId
                        )
                        item.images?.forEach { image ->
                            imageViewModel.insertImage(
                                imageId = image.imageId!!,
                                base64Image = image.base64Image!!,
                                item_id = item.itemId
                            )
                        }
                        println(item.person?.personId)
                        personViewModel.insertPerson(
                            item.person?.personId!!,
                            item.person.name!!
                        )
                    }

                    // Get updated items from the database
                    val localItems = itemDao.getAllItemsWithRelation().first()

                    // Update state with the newly received items
                    _uiState.update {
                        it.copy(
                            items = localItems,
                            isLoading = false,
                            response = serverResponse.response,
                            success = true
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle posible SQL exceptions
                AppLogger.e(
                    ProcessTags.HTTP_REQUEST_STARTED.name,
                    """
                        Process: ${ServiceTags.GET_ALL_ITEMS.name}.
                        Status: Attempting connection.
                    """.trimIndent(),
                    e
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        response = "Error: ${e.message}",
                        success = false
                    )
                }
            }
        }
    }
}
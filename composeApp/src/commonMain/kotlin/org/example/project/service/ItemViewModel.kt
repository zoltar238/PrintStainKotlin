package org.example.project.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.logging.LoggingTags
import org.example.project.persistence.ItemDaoImpl
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.repository.ItemDao
import org.example.project.persistence.repository.responseHandler
import org.example.project.persistence.DriverFactory


class ItemViewModel(private val db: PrintStainDatabase) : ViewModel() {

    private val driverFactory: DriverFactory = DriverFactory()

    private val itemDao: ItemDao = ItemDaoImpl(db)

    private val _items = MutableStateFlow(false)
    val items = _items.asStateFlow()

    init {
        _items.value = true
    }


    fun getAllItems() {
        viewModelScope.launch(Dispatchers.Default) {
            // Get access token
            val token = PreferencesManager.getToken()

            // Get all items from server
            val serverResponse = responseHandler(
                "Get all items from server",
                LoggingTags.ItemsGetAll.name,
                "List"
            ) { ClientController.itemController.getAllItems("Bearer $token") }

            serverResponse.data.forEach{item ->

            }

//            val itemDaoImpl: ItemDaoImpl = ItemDaoImpl();

            // Map images to realm objects
//            val items: MutableList<ItemDto> = mutableListOf()
//            if (serverResponse.data.isNotEmpty()) {
//                serverResponse.data.forEach { item ->
//                    items.add(mapItemDtoToRealm(item))
//                }
//            }

            //saveOrUpdateItems(items)
            //idreturn serverResponse
        }
    }

}
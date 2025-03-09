package org.example.project.service

import org.example.project.model.dto.ItemDto
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.controller.ClientController
import org.example.project.logging.LoggingTags
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.repository.initRealm
import org.example.project.persistence.repository.responseHandler

class ItemViewModel : ViewModel() {

    private val setting by lazy {
        initRealm()
    }

    private val _items = MutableStateFlow(false)
    val items = _items.asStateFlow()

    init {
        _items.value = true
    }


    fun getAllItems() {
        viewModelScope.launch {
            // Get access token
            val token = PreferencesManager.getToken()

            // Get all items from server
            val serverResponse = responseHandler(
                "Get all items from server",
                LoggingTags.ItemsGetAll.name,
                "List"
            ) { ClientController.itemController.getAllItems("Bearer $token") }



            // Decode base64 images to bitmap
//            if (serverResponse.data.isNotEmpty()) {
//                serverResponse.data.forEach { item ->
//                    item.base64Images?.forEach { image ->
//                        item.bitmapImages.add(decodeBase64ToBitmap(image))
//                    }
//                }
//            }
            //idreturn serverResponse
        }
    }

    private fun saveOrUpdateItems(items: List<ItemDto>) {
        //todo
    }

    override fun onCleared() {
        super.onCleared()
        setting.close()
    }
}
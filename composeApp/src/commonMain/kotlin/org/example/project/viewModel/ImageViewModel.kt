package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.persistence.database.ImageDaoImpl

class ImageViewModel(database: PrintStainDatabase) : ViewModel() {

    private val imageDao = ImageDaoImpl(database)

    fun insertImage(
        imageId: Long,
        base64Image: String,
        item_id: Long,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            imageDao.insertImage(imageId, base64Image, item_id)
        }
    }
}
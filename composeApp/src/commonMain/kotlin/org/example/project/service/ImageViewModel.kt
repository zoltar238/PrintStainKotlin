package org.example.project.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.persistence.DriverFactory
import org.example.project.persistence.repository.ImageDaoImpl

class ImageViewModel : ViewModel() {

    private val db: DriverFactory = DriverFactory()
    private val database = PrintStainDatabase.invoke(driver = db.createDriver())
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
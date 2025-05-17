package org.example.project.service

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.FileDto
import org.example.project.model.dto.ImageDto
import org.example.project.model.dto.ItemDto
import org.example.project.model.dto.ItemWithRelations
import org.example.project.persistence.database.*
import org.example.project.persistence.preferences.PreferencesDaoImpl
import org.example.project.util.Zipper
import org.example.project.util.encodeBitmapToBase64
import java.io.File

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
                fileStructure = item.fileStructure,
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

    fun createItemDto(name: String, description: String, images: List<ImageBitmap>): ItemDto {
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

    suspend fun updateItemFiles(files: MutableList<FileDto>): List<FileDto> {
        val selectedPlatformFiles = FileKit.openFilePicker(
            title = "Select model files",
            mode = FileKitMode.Multiple(),
        )
        selectedPlatformFiles?.forEach { file ->
            if (!files.any { it.fileName == file.file.name || it.fileUrl == file.file.absolutePath } && file.file.exists()) {
                files.add(FileDto(fileName = file.file.name, fileUrl = file.file.absolutePath))
            }
        }
        return files.toList()
    }

    suspend fun uploadFiles(files: List<FileDto>, itemId: Long, zipName: String): ResponseApi<String> {
        val token = PreferencesDaoImpl.getToken()
        val zipFilePath = "${System.getProperty("user.home")}/Downloads/${zipName}.zip"
        val zipFile = Zipper.createZip(files.map { File(it.fileUrl!!) }, File(zipFilePath))

        if (zipFile.exists()) {
            AppLogger.i(
                tag = ProcessTags.UploadItemFiles.name,
                "Zip file created: ${zipFile.absolutePath}, Size: ${zipFile.length()} bytes",
            )
        } else {
            AppLogger.e(
                tag = ProcessTags.UploadItemFiles.name,
                message = "Zip file NOT created or does not exist: ${zipFile.absolutePath}",
                throwable = Throwable("Zip file creation failed")
            )
            throw Throwable("Zip file creation failed")
        }

        if (zipFile.length() == 0L) {
            AppLogger.w(message = "Zip file is EMPTY: ${zipFile.absolutePath}", tag = ProcessTags.UploadItemFiles.name)
        }

        val requestFile = zipFile.asRequestBody("application/zip".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", zipFile.name, requestFile)


        AppLogger.i(
            message = "MultipartBody.Part: name='${body.headers?.get("Content-Disposition")}', filename='${zipFile.name}'",
            tag = ProcessTags.UploadItemFiles.name
        )

        val serverResponse = responseHandler(
            "Upload files",
            ProcessTags.UploadItemFiles.name,
        ) {
            ClientController.itemController.upload(
                "Bearer $token",
                file = body,
                itemId = itemId,
                fileStructure = Json.encodeToString(files)
            )
        }

        // Delete zip file from device
        zipFile.delete()

        return serverResponse
    }

    suspend fun updateFileStructure(itemId: Long, files: List<FileDto>) {
        itemDao.uploadFileStructure(
            itemId = itemId,
            fileStructure = "\"" + Json.encodeToString(files) + "\""
        )
    }

    suspend fun deleteFiles(itemId: Long): ResponseApi<String> {
        val token = PreferencesDaoImpl.getToken()

        val serverResponse = responseHandler(
            "Delete item files",
            ProcessTags.UploadItemFiles.name,
        ) {
            ClientController.itemController.deleteFiles(
                "Bearer $token",
                itemId = itemId,
            )
        }
        if (serverResponse.success) {
            itemDao.uploadFileStructure(itemId, "")
        }
        return serverResponse
    }

    suspend fun downloadFiles(itemId: Long, itemName: String): String {
        val token = PreferencesDaoImpl.getToken()
        val downloadDir = "${System.getProperty("user.home")}/Downloads"
        val downloadFile = File("$downloadDir/$itemName.zip")

        AppLogger.i(
            tag = ProcessTags.DownloadItemFiles.name,
            "Attempting to download files for itemId: $itemId, itemName: $itemName, download path: ${downloadFile.absolutePath}"
        )

        val serverResponse =
            ClientController.itemController.download(
                token = "Bearer $token",
                itemId = itemId,
            )


        serverResponse.body()?.let { responseBody ->
            downloadFile.outputStream().use { fileOut ->
                responseBody.byteStream().use { inputStream ->
                    inputStream.copyTo(fileOut)
                }
            }
            AppLogger.i(
                tag = ProcessTags.DownloadItemFiles.name,
                "File downloaded successfully to: ${downloadFile.absolutePath}"
            )
            return "File downloaded successfully to: ${downloadFile.absolutePath}"
        }

        throw IllegalStateException("Could not download file")
    }
}
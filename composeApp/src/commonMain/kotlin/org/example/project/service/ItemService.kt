package org.example.project.service

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.model.dto.FileDto
import org.example.project.model.dto.ImageDto
import org.example.project.model.dto.ItemDto
import org.example.project.model.dto.ItemWithRelations
import org.example.project.persistence.database.*
import org.example.project.persistence.preferences.PreferencesDaoImpl
import org.example.project.util.Zipper
import org.example.project.util.encodeBitmapToBase64
import java.io.File
import java.io.IOException

class ItemService(database: PrintStainDatabase) {
    private val itemDao: ItemDao = ItemDaoImpl(database)
    private val imageDao: ImageDao = ImageDaoImpl(database)
    private val personDao: PersonDao = PersonDaoImpl(database)

    suspend fun fetchAllItemsFromServer(): ResponseApi<List<ItemDto>> {
        val processCode = "000019"
        val processName = "Fetch all items from server"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to fetch all items.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained. Calling server.")
            val response = responseHandler { ClientController.itemController.getAllItems("Bearer $token") }
            if (response.success) {
                val itemCount = response.data?.size ?: 0
                AppLogger.d("[MSG-$processCode: $processName] -> Successfully fetched $itemCount items from server.")

                // Process items to the local database
                response.data?.let { items ->
                    AppLogger.d("[DBG-$processCode: $processName] -> Processing $itemCount items to local database.")
                    processItemsToLocalDB(items)
                    AppLogger.d("[DBG-$processCode: $processName] -> Items successfully processed to local database.")
                }

                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully fetched and processed items from server.")
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Failed to fetch items from server: ${response.response}.")
            }
            response
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error: ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(
                success = false,
                response = "Unexpected error fetching items from server: ${e.localizedMessage}",
                data = null
            )
        }
    }

    suspend fun createItemOnServer(itemDto: ItemDto): ResponseApi<ItemDto> {
        val processCode = "000020"
        val processName = "Create item on server"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to create item: ${itemDto.name}.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained. Item DTO: ${itemDto.name}. Calling server.")
            val response = responseHandler { ClientController.itemController.postItem("Bearer $token", itemDto) }
            if (response.success) {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully created item '${itemDto.name}' on server.")
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Failed to create item '${itemDto.name}' on server: ${response.response}.")
            }
            response
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error creating item '${itemDto.name}': ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(
                success = false,
                response = "Unexpected error creating item: ${e.localizedMessage}",
                data = null
            )
        }
    }

    suspend fun updateItemOnServer(itemDto: ItemDto): ResponseApi<ItemDto> {
        val processCode = "000021"
        val processName = "Update item on server"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to update item ID: ${itemDto.itemId}, Name: ${itemDto.name}.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained. Item DTO: ${itemDto.name}. Calling server.")
            val response = responseHandler { ClientController.itemController.updateItem("Bearer $token", itemDto) }
            if (response.success) {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully updated item ID: ${itemDto.itemId} on server. Response data: ${response.response}")
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Failed to update item ID: ${itemDto.itemId} on server: ${response.response}.")
            }
            response
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error updating item ID: ${itemDto.itemId}: ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(
                success = false,
                response = "Unexpected error updating item: ${e.localizedMessage}",
                data = null
            )
        }
    }

    suspend fun deleteItemsOnServer(items: List<ItemDto>): ResponseApi<String> {
        val processCode = "000022"
        val processName = "Delete items on server"
        val itemIds = items.mapNotNull { it.itemId }
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to delete ${items.size} items with IDs: $itemIds.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained. Items to delete: ${items.size}. Calling server.")
            val response = responseHandler { ClientController.itemController.deleteItems("Bearer $token", items) }
            if (response.success) {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully deleted items on server.")
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Failed to delete items on server: ${response.response}.")
            }
            response
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error deleting items: ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(
                success = false,
                response = "Unexpected error deleting items: ${e.localizedMessage}",
                data = null
            )
        }
    }

    suspend fun updateItemLocally(items: List<ItemDto>) {
        val processCode = "000023"
        val processName = "Process item images to local DB"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Processing images for ${items.size} items to local DB.")
        try {
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
                AppLogger.d("[DBG-$processCode: $processName] -> Inserted/Updated item ID: ${item.itemId}.")

                item.images?.forEach { image ->
                    imageDao.insertImage(
                        imageId = image.imageId ?: System.currentTimeMillis(),
                        base64Image = image.base64Image ?: "",
                        item_id = item.itemId
                    )
                }
            }
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error processing images to local DB: ${e.localizedMessage}.",
                throwable = e
            )
        }
    }

    suspend fun processItemsToLocalDB(items: List<ItemDto>) {
        val processCode = "000023"
        val processName = "Process items to local DB"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Processing ${items.size} items to local DB.")
        try {

            //Save only new items
            val databaseItems = itemDao.getAllItemsWithRelation().firstOrNull() ?: emptyList()

            val newItems = items.filter { itemDto ->
                itemDto.itemId !in databaseItems.map { it.item.itemId }
            }

            newItems.forEach { item ->
                AppLogger.d("[DBG-$processCode: $processName] -> Processing item ID: ${item.itemId}, Name: ${item.name}.")
                item.itemId?.let { itemId ->
                    itemDao.insertItem(
                        itemId = itemId,
                        name = item.name,
                        description = item.description,
                        postDate = item.postDate.toString(),
                        fileStructure = item.fileStructure,
                        timesUploaded = item.timesUploaded,
                        personId = item.person?.personId
                    )
                    AppLogger.d("[DBG-$processCode: $processName] -> Inserted/Updated item ID: $itemId.")

                    item.images?.forEachIndexed { index, image ->
                        val imageId = image.imageId ?: System.currentTimeMillis()
                        if (image.imageId == null) {
                            AppLogger.w("[MSG-$processCode: $processName - Process] -> Image at index $index had null ID. Assigned new ID: $imageId.")
                        } else {
                            AppLogger.d("[DBG-$processCode: $processName] -> Processing image ID: $imageId for item ID: $itemId.")
                        }

                        imageDao.insertImage(
                            imageId = imageId,
                            // default to empty if null
                            base64Image = image.base64Image ?: "",
                            item_id = itemId
                        )
                        AppLogger.d("[DBG-$processCode: $processName] -> Inserted/Updated image ID: $imageId.")
                    }

                    item.person?.let { person ->
                        person.personId?.let { personId ->
                            AppLogger.d("[DBG-$processCode: $processName] -> Processing person ID: $personId, Name: ${person.name}.")
                            personDao.insertPerson(
                                personId = personId,
                                name = person.name ?: "Unknown", // Handle potential null name
                                username = person.username,
                                isActive = person.isActive ?: true
                            )
                            AppLogger.d("[DBG-$processCode: $processName] -> Inserted/Updated person ID: $personId.")
                        }
                            ?: AppLogger.w("[MSG-$processCode: $processName - Process] -> Skipped person with null ID for item ID: $itemId.")
                    }
                }
                    ?: AppLogger.w("[MSG-$processCode: $processName - Process] -> Skipped item with null ID: ${item.name}.")
            }
            AppLogger.i("[MSG-$processCode: $processName - End of process] -> Finished processing items to local DB.")
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error processing items to local DB: ${e.localizedMessage}.",
                throwable = e
            )
        }
    }

    fun getAllLocalItems(): Flow<List<ItemWithRelations>> {
        val processCode = "000024"
        val processName = "Get all local items flow"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Setting up flow for local items with relations.")

        return itemDao.getAllItemsWithRelation().onEach { items ->
            AppLogger.d("[MSG-$processCode: $processName] -> Flow emitted ${items.size} items from local DB.")
        }
    }

    private val _selectedItemId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedItem: Flow<ItemWithRelations?> = _selectedItemId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                itemDao.getItemWithRelationsById(id)
            }
        }

    fun selectItem(id: Long?) {
        _selectedItemId.value = id
    }


    suspend fun getItemById(id: Long): ItemWithRelations? {
        val processCode = "000025"
        val processName = "Get item by ID locally"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to get item by ID: $id.")
        return try {
            // getAllLocalItems already has logging and error handling
            val item = itemDao.getAllItemsWithRelation().first().firstOrNull { it.item.itemId == id }
            if (item != null) {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Item found for ID: $id.")
            } else {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> No item found for ID: $id.")
            }
            item
        } catch (e: Exception) { // Catching exceptions from the find operation itself, though getAllLocalItems handles DB errors.
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error finding item by ID $id: ${e.localizedMessage}.",
                throwable = e
            )
            null
        }
    }

    suspend fun deleteLocalItems(items: List<ItemWithRelations>) {
        val processCode = "000026"
        val processName = "Delete local items"
        val itemIds = items.map { it.item.itemId }
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to delete ${items.size} local items with IDs: $itemIds.")
        try {
            items.forEach { item ->
                AppLogger.d("[DBG-$processCode: $processName] -> Deleting local item ID: ${item.item.itemId}.")
                itemDao.deleteItem(item.item.itemId) // Assuming itemId is not nullable here
                AppLogger.d("[DBG-$processCode: $processName] -> Successfully deleted local item ID: ${item.item.itemId}.")
            }
            AppLogger.i("[MSG-$processCode: $processName - End of process] -> Finished deleting local items.")
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error deleting local items: ${e.localizedMessage}.",
                throwable = e
            )
        }
    }

    fun createItemDto(name: String, description: String, images: List<ImageBitmap>): ItemDto {
        val processCode = "000027"
        val processName = "Create ItemDto"
        AppLogger.d(
            "[DBG-$processCode: $processName] -> Creating ItemDto with name: $name, description: ${
                description.take(
                    50
                )
            }..., image count: ${images.size}."
        )
        val itemDto = ItemDto(
            name = name,
            description = description,
            images = images.filter { it.width > 1 && it.height > 1 }
                .map {
                    AppLogger.d("[DBG-$processCode: $processName] -> Encoding image of size ${it.width}x${it.height}.")
                    ImageDto(base64Image = encodeBitmapToBase64(it))
                }
        )
        AppLogger.d("[DBG-$processCode: $processName] -> ItemDto created: Name: ${itemDto.name}, Image DTOs: ${itemDto.images?.size ?: 0}.")
        return itemDto
    }

    suspend fun deleteImagesForItem(itemId: Long) {
        val processCode = "000028"
        val processName = "Delete images for item locally"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to delete images for item ID: $itemId from local DB.")
        try {
            imageDao.deleteImagesById(itemId)
            AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully deleted images for item ID: $itemId from local DB.")
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error deleting images for item ID $itemId: ${e.localizedMessage}.",
                throwable = e
            )
        }
    }

    suspend fun updateItemFiles(files: MutableList<FileDto>): MutableList<FileDto> {
        val processCode = "000029"
        val processName = "Update item files (FilePicker)"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Opening file picker to update item files.")
        try {
            val selectedPlatformFiles = FileKit.openFilePicker(
                title = "Select model files",
                mode = FileKitMode.Multiple(),
            )
            AppLogger.d("[DBG-$processCode: $processName] -> File picker returned ${selectedPlatformFiles?.size ?: 0} files.")
            selectedPlatformFiles?.forEach { platformFile ->
                AppLogger.d("[DBG-$processCode: $processName] -> Selected file: Name: ${platformFile.file.name}, Path: ${platformFile.file.absolutePath}, Exists: ${platformFile.file.exists()}.")
                if (!files.any { it.fileName == platformFile.file.name || it.fileUrl == platformFile.file.absolutePath } && platformFile.file.exists()) {
                    val newFileDto =
                        FileDto(fileName = platformFile.file.name, fileUrl = platformFile.file.absolutePath)
                    files.add(newFileDto)
                    AppLogger.d("[DBG-$processCode: $processName] -> Added new file to list: ${newFileDto.fileName}.")
                } else {
                    AppLogger.d("[DBG-$processCode: $processName] -> File already exists in list or does not exist on disk: ${platformFile.file.name}.")
                }
            }
            AppLogger.i("[MSG-$processCode: $processName - End of process] -> File list updated. Total files: ${files.size}.")
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error during file picking process: ${e.localizedMessage}.",
                throwable = e
            )
        }
        return files
    }

    suspend fun uploadFiles(files: List<FileDto>, itemId: Long, zipName: String): ResponseApi<String> {
        val processCode = "000030"
        val processName = "Upload item files"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to upload ${files.size} files for item ID: $itemId as '$zipName.zip'.")

        if (files.isEmpty()) {
            AppLogger.w("[MSG-$processCode: $processName - Process] -> No files to upload for item ID: $itemId.")
            return ResponseApi(
                success = false,
                response = "No files selected for upload.",
                data = "No files to upload."
            )
        }

        val zipFilePath = "${System.getProperty("user.home")}/Downloads/${zipName}.zip"
        val zipFile = File(zipFilePath)

        try {
            AppLogger.d("[DBG-$processCode: $processName] -> Creating zip file at: $zipFilePath.")
            Zipper.createZip(files.mapNotNull { it.fileUrl?.let { File(it) } }, zipFile)

            if (zipFile.exists()) {
                AppLogger.i("[MSG-$processCode: $processName - Process] -> Zip file created: ${zipFile.absolutePath}, Size: ${zipFile.length()} bytes.")
            } else {
                AppLogger.w(message = "[MSG-$processCode: $processName - End of process] -> Zip file NOT created or does not exist: ${zipFile.absolutePath}.")
                return ResponseApi(
                    success = false,
                    response = "Zip file creation failed at $zipFilePath",
                    data = "Zip file creation failed"
                )
            }

            if (zipFile.length() == 0L) {
                AppLogger.w("[MSG-$processCode: $processName - Process] -> Zip file is EMPTY: ${zipFile.absolutePath}.")
                // Decide if this is a hard error or not. For now, proceeding.
            }

            AppLogger.d("[DBG-$processCode: $processName] -> Preparing multipart body for zip file.")
            val requestFile = zipFile.asRequestBody("application/zip".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", zipFile.name, requestFile)
            AppLogger.d(
                "[DBG-$processCode: $processName] -> MultipartBody.Part created. Name: '${
                    body.headers?.get(
                        "Content-Disposition"
                    )
                }', Filename: '${zipFile.name}'."
            )

            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained. Calling server to upload files.")

            val fileStructureJson = Json.encodeToString(files)
            AppLogger.d("[DBG-$processCode: $processName] -> File structure JSON: $fileStructureJson")

            val serverResponse = responseHandler {
                ClientController.itemController.upload(
                    "Bearer $token",
                    file = body,
                    itemId = itemId,
                    fileStructure = fileStructureJson
                )
            }

            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Files uploaded successfully for item ID: $itemId.")
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Failed to upload files for item ID: $itemId. Server error: ${serverResponse.response}")
            }
            return serverResponse

        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error during file upload for item ID $itemId: ${e.localizedMessage}.",
                throwable = e
            )
            return ResponseApi(
                success = false,
                response = "Unexpected error uploading files: ${e.localizedMessage}",
                data = null
            )
        } finally {
            if (zipFile.exists()) {
                AppLogger.d("[DBG-$processCode: $processName] -> Deleting temporary zip file: ${zipFile.absolutePath}")
                if (zipFile.delete()) {
                    AppLogger.d("[DBG-$processCode: $processName] -> Temporary zip file deleted successfully.")
                } else {
                    AppLogger.w("[MSG-$processCode: $processName - Process] -> Failed to delete temporary zip file: ${zipFile.absolutePath}.")
                }
            }
        }
    }

    suspend fun updateFileStructure(itemId: Long, files: List<FileDto>) {
        val processCode = "000031"
        val processName = "Update file structure locally"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to update file structure for item ID: $itemId with ${files.size} files.")
        try {
            val fileStructureJson = Json.encodeToString(files)
            val finalJsonString = "\"$fileStructureJson\""
            AppLogger.d("[DBG-$processCode: $processName] -> Updating item ID: $itemId with file structure: $finalJsonString.")
            itemDao.uploadFileStructure(
                itemId = itemId,
                fileStructure = finalJsonString
            )
            AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully updated file structure for item ID: $itemId.")
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error updating file structure for item ID $itemId: ${e.localizedMessage}.",
                throwable = e
            )
        }
    }

    suspend fun deleteFiles(itemId: Long): ResponseApi<String> {
        val processCode = "000032"
        val processName = "Delete files on server and update local structure"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to delete files for item ID: $itemId on server.")
        try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained. Calling server to delete files for item ID: $itemId.")

            val serverResponse = responseHandler {
                ClientController.itemController.deleteFiles(
                    "Bearer $token",
                    itemId = itemId
                )
            }

            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - Process] -> Files deleted successfully on server for item ID: $itemId.")
                AppLogger.d("[DBG-$processCode: $processName] -> Updating local file structure to empty for item ID: $itemId.")
                itemDao.uploadFileStructure(itemId, "\"[]\"")
                AppLogger.d("[DBG-$processCode: $processName] -> Local file structure updated.")
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully deleted files on server and cleared local structure for item ID: $itemId.")
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Failed to delete files on server for item ID: $itemId. Error: ${serverResponse.response}.")
            }
            return serverResponse
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error deleting files for item ID $itemId: ${e.localizedMessage}.",
                throwable = e
            )
            return ResponseApi(
                success = false,
                response = "Unexpected error deleting files: ${e.localizedMessage}",
                data = null
            )
        }
    }

    suspend fun downloadFiles(itemId: Long, itemName: String): ResponseApi<String> {
        val processCode = "000033"
        val processName = "Download item files"
        val downloadDir = "${System.getProperty("user.home")}/Downloads"
        val downloadFile = File("$downloadDir/$itemName.zip")

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to download files for itemId: $itemId, itemName: $itemName. Path: ${downloadFile.absolutePath}")
        try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained. Calling server to download files.")

            val serverCallResponse = ClientController.itemController.download(
                token = "Bearer $token",
                itemId = itemId
            )

            AppLogger.d("[DBG-$processCode: $processName] -> Server call completed. Response code: ${serverCallResponse.code()}, Is successful: ${serverCallResponse.isSuccessful}")

            if (serverCallResponse.isSuccessful) {
                serverCallResponse.body()?.let { responseBody ->
                    AppLogger.d("[DBG-$processCode: $processName] -> Response body received. Writing to file: ${downloadFile.absolutePath}")
                    downloadFile.outputStream().use { fileOut ->
                        responseBody.byteStream().use { inputStream ->
                            val bytesCopied = inputStream.copyTo(fileOut)
                            AppLogger.d("[DBG-$processCode: $processName] -> Copied $bytesCopied bytes to file.")
                        }
                    }
                    val successMsg = "File downloaded successfully to: ${downloadFile.absolutePath}"
                    AppLogger.i("[MSG-$processCode: $processName - End of process] -> $successMsg")
                    return ResponseApi(success = true, response = successMsg, data = successMsg)
                } ?: run {
                    val errorMsg =
                        "Server returned successful status but response body was null for item ID $itemId."
                    AppLogger.w("[MSG-$processCode: $processName - End of process] -> $errorMsg")
                    return ResponseApi(success = false, response = errorMsg, data = errorMsg)
                }
            } else {
                val errorBody = serverCallResponse.errorBody()?.string() ?: "No error body"
                val errorMsg =
                    "Failed to download file. Server responded with code: ${serverCallResponse.code()}, Message: ${serverCallResponse.message()}, Error: $errorBody"
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> $errorMsg")
                return ResponseApi(success = false, response = errorMsg, data = errorMsg)
            }
        } catch (e: IOException) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> IO error downloading files for item ID $itemId: ${e.localizedMessage}.",
                throwable = e
            )
            return ResponseApi(
                success = false,
                response = "IO error downloading file: ${e.localizedMessage}",
                data = "IO error downloading file: ${e.localizedMessage}"
            )
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error downloading files for item ID $itemId: ${e.localizedMessage}.",
                throwable = e
            )
            return ResponseApi(
                success = false,
                response = "Unexpected error downloading file: ${e.localizedMessage}",
                data = "Unexpected error downloading file: ${e.localizedMessage}"
            )
        }
    }

    fun previewFile(path: String): ResponseApi<String> {
        val processCode = "000034"
        val processName = "Preview file with fstl"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to open fstl with path: $path")
        try {
            val fileToPreview = File(path)
            AppLogger.d("[DBG-$processCode: $processName] -> Checking if file exists: ${fileToPreview.absolutePath}")
            if (!fileToPreview.exists()) {
                AppLogger.w("[MSG-$processCode: $processName - Process] -> File not found: $path")
                throw Exception("File was not found in the system at $path")
            }
            AppLogger.d("[DBG-$processCode: $processName] -> File exists. Checking fstl installation.")

            val osName = System.getProperty("os.name").lowercase()
            val checkCommand = if (osName.contains("windows")) "where" else "which"

            AppLogger.d("[DBG-$processCode: $processName] -> Using '$checkCommand fstl' to find fstl path.")
            val findFstlProcess = Runtime.getRuntime().exec(arrayOf(checkCommand, "fstl"))
            val fstlPath = findFstlProcess.inputStream.bufferedReader().readLine()
            val findExitCode = findFstlProcess.waitFor()

            if (findExitCode != 0 || fstlPath.isNullOrBlank()) {
                AppLogger.w("[MSG-$processCode: $processName - Process] -> '$checkCommand fstl' failed (exit code: $findExitCode) or returned empty path. fstl might not be installed or not in PATH.")
                val errorOutput = findFstlProcess.errorStream.bufferedReader().readText()
                if (errorOutput.isNotBlank()) {
                    AppLogger.d("[DBG-$processCode: $processName] -> Error stream from '$checkCommand fstl': $errorOutput")
                }
                throw Exception("fstl is not installed or not found in system PATH.")
            }
            AppLogger.i("[MSG-$processCode: $processName - Process] -> fstl found at: $fstlPath. Proceeding to open file.")

            val command = arrayOf(fstlPath, path)
            AppLogger.d("[DBG-$processCode: $processName] -> Executing command: ${command.joinToString(" ")}.")
            Runtime.getRuntime().exec(command) // This launches asynchronously

            AppLogger.i("[MSG-$processCode: $processName - End of process] -> fstl launched to preview file: $path.")
            return ResponseApi(
                success = true,
                response = "File preview opened successfully using fstl.",
                data = "File preview opened successfully"
            )
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt() // Restore interruption status
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Process interrupted while checking/launching fstl: ${e.localizedMessage}.",
                throwable = e
            )
            return ResponseApi(
                false,
                "Process interrupted: ${e.localizedMessage}",
                "Error opening file preview: Interrupted"
            )
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Error attempting to open fstl with path '$path': ${e.localizedMessage}.",
                throwable = e
            )
            return ResponseApi(
                false,
                "Error opening file preview: ${e.localizedMessage}",
                "Error opening file preview"
            )
        }
    }
}
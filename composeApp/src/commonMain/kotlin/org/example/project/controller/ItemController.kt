package org.example.project.controller

import kotlinx.io.files.FileMetadata
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.example.project.model.dto.ItemDto
import org.jetbrains.compose.resources.Resource
import retrofit2.Response
import retrofit2.http.*

interface ItemController {
    @GET("item/getAllItems")
    suspend fun getAllItems(@Header("Authorization") token: String): Response<ResponseApi<List<ItemDto>>>

    @GET("item/getAllUserItems")
    suspend fun getAllUserItems(@Header("Authorization") token: String): Response<ResponseApi<List<ItemDto>>>

    @POST("item/postItem")
    suspend fun postItem(@Header("Authorization") token: String, @Body itemDto: ItemDto): Response<ResponseApi<ItemDto>>

    @HTTP(method = "DELETE", path = "item/deleteItems", hasBody = true)
    suspend fun deleteItems(
        @Header("Authorization") token: String,
        @Body itemDtos: List<ItemDto>,
    ): Response<ResponseApi<String>>

    @PUT("item/updateItem")
    suspend fun updateItem(
        @Header("Authorization") token: String,
        @Body itemDto: ItemDto,
    ): Response<ResponseApi<ItemDto>>

    @Multipart
    @POST("item/upload")
    suspend fun upload(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("itemId") itemId: Long,
        @Part("fileStructure") fileStructure: String,
    ): Response<ResponseApi<String>>

    @GET("item/download")
    suspend fun download(
        @Header("Authorization") token: String,
        @Query("itemId") itemId: Long
    ): Response<ResponseBody>

    @DELETE("/item/deleteFiles")
    suspend fun deleteFiles(
        @Header("Authorization") token: String,
        @Query("itemId") itemId: Long,
    ): Response<ResponseApi<String>>
}
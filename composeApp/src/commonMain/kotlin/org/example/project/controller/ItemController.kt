package org.example.project.controller

import org.example.project.model.dto.ItemDto
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
    suspend fun deleteItems(@Header("Authorization") token: String, @Body itemDtos: List<ItemDto>): Response<ResponseApi<String>>

    @PUT("item/updateItem")
    suspend fun updateItem(@Header("Authorization") token: String, @Body itemDto: ItemDto): Response<ResponseApi<ItemDto>>
}
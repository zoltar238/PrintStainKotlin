package org.example.project.persistence.network

import org.example.project.model.ItemDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ItemsApi {
    @GET("item/getAllItems")
    suspend fun getAllItems(@Header("Authorization") token: String): Response<ResponseApi<List<ItemDto>>>

    @GET("item/getAllUserItems")
    suspend fun getAllUserItems(@Header("Authorization") token: String): Response<ResponseApi<List<ItemDto>>>
}
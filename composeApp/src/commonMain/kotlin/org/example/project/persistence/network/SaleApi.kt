package org.example.project.persistence.network

import org.example.project.model.SaleDto
import retrofit2.http.*

interface SaleApi {
    @POST("sale/newSale")
    suspend fun createNewSale(
        @Body saleDto: SaleDto,
        @Header("Authorization") token: String
    ): String

    @GET("sale/getAllSales")
    suspend fun getAllSales(
        @Header("Authorization") token: String
    ): String

    @DELETE("sale/deleteSale")
    suspend fun deleteSale(
        @Body saleDto: SaleDto,
        @Header("Authorization") token: String
    ): String
}

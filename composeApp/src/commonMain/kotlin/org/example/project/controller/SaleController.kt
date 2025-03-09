package org.example.project.controller

import org.example.project.model.dto.AllSalesDto
import org.example.project.model.dto.SaleDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SaleController {
    @POST("sale/newSale")
    suspend fun createNewSale(
        @Body saleDto: SaleDto,
        @Header("Authorization") token: String,
    ): Response<ResponseApi<String>>

    @GET("sale/getAllSales")
    suspend fun findAllSales(
        @Header("Authorization") token: String,
    ): Response<ResponseApi<List<AllSalesDto>>>

    @DELETE("sale/deleteSale")
    suspend fun deleteSale(
        @Body saleDto: SaleDto,
        @Header("Authorization") token: String,
    ): String
}

package org.example.project.persistence.database

import comexampleproject.Sale
import kotlinx.coroutines.flow.Flow

interface SaleDao {

    suspend fun insertSale(
        saleId: Long,
        date: String?,
        cost: Double?,
        price: Double?,
        itemId: Long?,
    )

    fun getALlSales(): Flow<List<Sale>>

    suspend fun deleteSale(saleId: Long)
}
package org.example.project.persistence.database

import comexampleproject.Sale
import kotlinx.coroutines.flow.Flow

interface SaleDao {

    fun getALlSales(): Flow<List<Sale>>

    suspend fun deleteSale(saleId: Long)

    fun getSaleById(saleId: Long): Flow<Sale>

    suspend fun updateSale(
        saleId: Long,
        cost: Double?,
        price: Double?,
        status: String?
    )

    suspend fun insertSale(saleId: Long, date: String?, cost: Double?, price: Double?, itemId: Long?, status: String?)
}
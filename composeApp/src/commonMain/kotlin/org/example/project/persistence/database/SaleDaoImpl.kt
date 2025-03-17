package org.example.project.persistence.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import comexampleproject.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.example.project.PrintStainDatabase

class SaleDaoImpl(db: PrintStainDatabase) : SaleDao {

    private val query = db.saleEntityQueries

    override suspend fun insertSale(
        saleId: Long,
        date: String?,
        cost: Double?,
        price: Double?,
        itemId: Long?,
    ) {
        query.insertOrReplaceSale(
            saleId = saleId,
            date = date,
            cost = cost,
            price = price,
            itemId = itemId
        )
    }

    override fun getALlSales(): Flow<List<Sale>> {
        return query.selectAllSales().asFlow().mapToList(Dispatchers.IO)
    }
}
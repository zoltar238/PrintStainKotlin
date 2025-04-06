package org.example.project.persistence.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
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
        status: String?,
    ) {
        query.insertOrReplaceSale(
            saleId = saleId,
            date = date,
            cost = cost,
            price = price,
            itemId = itemId,
            status = status
        )
    }

    override fun getALlSales(): Flow<List<Sale>> {
        return query.selectAllSales().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun deleteSale(saleId: Long) {
        query.deleteSaleById(saleId)
    }

    override fun getSaleById(saleId: Long): Flow<Sale> {
        return query.selectSaleById(saleId).asFlow().mapToOne(Dispatchers.IO)
    }

    override suspend fun updateSale(saleId: Long, cost: Double?, price: Double?, status: String?) {
        query.updateSale(
            cost = cost,
            price = price,
            saleId = saleId,
            status = status,
        )
    }
}
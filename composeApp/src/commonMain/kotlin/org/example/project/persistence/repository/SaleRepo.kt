package org.example.project.persistence.repository

import org.example.project.model.SaleDto

interface SaleRepo {
    fun createNewSale(saleDto: SaleDto): String

    fun deleteSale(saleDto: SaleDto): String
}
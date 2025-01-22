package org.example.project.persistence.repository

import org.example.project.model.AllSalesDto
import org.example.project.model.SaleDto
import org.example.project.persistence.network.ResponseApi

interface SaleRepo {
    fun createNewSale(saleDto: SaleDto): ResponseApi<String>

    fun deleteSale(saleDto: SaleDto): String

    fun findAllSales(): ResponseApi<List<AllSalesDto>>
}
package org.example.project.persistence.repository

import org.example.project.model.dto.AllSalesDto
import org.example.project.model.dto.SaleDto
import org.example.project.controller.ResponseApi

interface SaleRepo {
    fun createNewSale(saleDto: SaleDto): ResponseApi<String>?

    fun deleteSale(saleDto: SaleDto): String

    fun findAllSales(): ResponseApi<List<AllSalesDto>>?
}
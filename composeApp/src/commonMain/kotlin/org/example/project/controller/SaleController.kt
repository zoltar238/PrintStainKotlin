package org.example.project.controller

import org.example.project.model.AllSalesDto
import org.example.project.service.findAllSales

object SaleController {
    var allSales: List<AllSalesDto> = emptyList()

    // load all sales
    // TODO: Make all sale loading only available for admin users
    fun findAllSalesController() {
        val serverResponse = findAllSales()
        allSales = serverResponse.data
    }
}
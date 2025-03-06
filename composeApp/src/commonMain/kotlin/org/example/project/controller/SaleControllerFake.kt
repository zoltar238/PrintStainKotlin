package org.example.project.controller

import org.example.project.model.AllSalesDto
import org.example.project.service.findAllSales

object SaleControllerFake {
    var allSales: List<AllSalesDto> = emptyList()

    // load all sales
    fun findAllSalesController() {
        val serverResponse = findAllSales()
        allSales = serverResponse.data
    }

}
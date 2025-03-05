package org.example.project.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.logging.AppLogger
import org.example.project.model.AllSalesDto
import org.example.project.model.SaleDto
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.repository.SaleRepoHttpImp
import org.http4k.core.Response

fun createNewSale(saleDto: SaleDto): ResponseApi<String> {
    // Receive the information from server
    val serverResponse = SaleRepoHttpImp.createNewSale(saleDto)
    return serverResponse!!
}

fun findAllSales(): ResponseApi<List<AllSalesDto>> {
    // Receive all sales from server
    val serverResponse = SaleRepoHttpImp.findAllSales()
    return serverResponse!!
}
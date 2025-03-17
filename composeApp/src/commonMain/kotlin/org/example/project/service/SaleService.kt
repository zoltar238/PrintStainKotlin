package org.example.project.service

import kotlinx.coroutines.runBlocking
import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.controller.responseHandler
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.SaleDto
import org.example.project.persistence.preferences.PreferencesManager

fun createNewSale(saleDto: SaleDto): ResponseApi<String> {
    // Get access token
    val token = runBlocking {
        PreferencesManager.getToken()
    }

    // Return the information from server
    return responseHandler(
        "Create new sale",
        ProcessTags.SaleCreateNew.name,
        "String",
    ) {
        ClientController.saleController.createNewSale(
            saleDto = saleDto,
            token = "Bearer $token"
        )
    }
}
package org.example.project.persistence.repository

import kotlinx.coroutines.runBlocking
import org.example.project.logging.LoggingTags
import org.example.project.model.AllSalesDto
import org.example.project.model.SaleDto
import org.example.project.persistence.network.ClientApi
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.preferences.PreferencesManager

object SaleRepoHttpImp : SaleRepo {
    override fun createNewSale(saleDto: SaleDto): ResponseApi<String>? {

        // Obtain access token
        val token = runBlocking {
            PreferencesManager.getToken()
        }

        return responseHandler(
            LoggingTags.SaleCreateNew.name,
            "String",
        ) {
            ClientApi.saleApi.createNewSale(
                saleDto = saleDto,
                token = "Bearer $token"
            )
        }
    }

    override fun deleteSale(saleDto: SaleDto): String {
        TODO("Not yet implemented")
    }

    override fun findAllSales(): ResponseApi<List<AllSalesDto>>? {
        // Obtain access token
        val token = runBlocking {
            PreferencesManager.getToken()
        }


        return responseHandler(
            LoggingTags.SaleFindAll.name,
            "String",
        ) {
            ClientApi.saleApi.findAllSales(
                token = "Bearer $token"
            )
        }
    }
}
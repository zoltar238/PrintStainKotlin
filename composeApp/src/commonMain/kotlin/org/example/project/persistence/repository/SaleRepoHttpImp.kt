package org.example.project.persistence.repository

import kotlinx.coroutines.runBlocking
import org.example.project.model.AllSalesDto
import org.example.project.model.SaleDto
import org.example.project.persistence.network.ClientApi
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.preferences.PreferencesManager

object SaleRepoHttpImp : SaleRepo {
    override fun createNewSale(saleDto: SaleDto): ResponseApi<String> {

        // Obtain access token
        val token = runBlocking {
            PreferencesManager.getToken()
        }

        return if (token != null) {
            runBlocking {
                ClientApi.saleApi.createNewSale(
                    saleDto = saleDto,
                    token = "Bearer $token"
                )
            }
        } else {
            return ResponseApi(false, "Error connecting to server", "Error connecting to server")
        }
    }

    override fun deleteSale(saleDto: SaleDto): String {
        TODO("Not yet implemented")
    }

    override fun findAllSales(): ResponseApi<List<AllSalesDto>> {
        /*
        return runBlocking {
            val token = PreferencesManager.getToken()
            ApiClient.saleApi.findAllSales("Bearer $token")
        }
         */
        // Obtain access token
        val token = runBlocking {
            PreferencesManager.getToken()
        }

        return if (token != null) {
            runBlocking {
                ClientApi.saleApi.findAllSales(
                    token = "Bearer $token"
                )
            }
        } else {
            return ResponseApi(false, "Error connecting to server", emptyList())
        }
    }
}
package org.example.project.service

import comexampleproject.Sale
import kotlinx.coroutines.flow.first
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.SaleDto
import org.example.project.persistence.database.SaleDao
import org.example.project.persistence.database.SaleDaoImpl
import org.example.project.persistence.preferences.PreferencesDaoImpl
import java.math.BigDecimal
import java.time.OffsetDateTime

class SaleService(
    database: PrintStainDatabase,
) {
    private val saleDao: SaleDao = SaleDaoImpl(database)

    suspend fun getAllSales(): ResponseApi<List<Sale>> {
        return try {
            // Obtain token
            val token = PreferencesDaoImpl.getToken()

            // Get items from server
            val serverResponse = responseHandler(
                "Get all sales from server",
                ProcessTags.SaleFindAll.name,
            ) {
                ClientController.saleController.findAllSales(
                    token = "Bearer $token"
                )
            }

            if (serverResponse.success) {
                // Save each received sale
                serverResponse.data!!.forEach { sale ->
                    saleDao.insertSale(
                        saleId = sale.saleId!!,
                        date = sale.date.toString(),
                        cost = sale.cost?.toDouble(),
                        price = sale.price?.toDouble(),
                        itemId = sale.itemId,
                        status = sale.status
                    )
                }

                // Get all saved sales
                val localSales = saleDao.getALlSales().first()
                ResponseApi(
                    success = true,
                    data = localSales,
                    response = serverResponse.response
                )
            } else {
                ResponseApi(
                    success = false,
                    data = null,
                    response = serverResponse.response
                )
            }
        } catch (e: Exception) {
            // Handle posible SQL exceptions
            AppLogger.e(
                ProcessTags.SaleFindAll.name,
                """
                    Process: Get all sales from server.
                    Status: Internal sql error loading all sales.
                """.trimIndent(),
                e
            )
            ResponseApi(
                success = false,
                data = null,
                response = "Error: ${e.localizedMessage}"
            )
        }
    }

    suspend fun createSale(cost: BigDecimal, price: BigDecimal, itemId: Long): ResponseApi<List<Sale>> {
        return try {
            // Get access token
            val token = PreferencesDaoImpl.getToken()

            val saleDto = SaleDto(
                cost = cost,
                price = price,
                itemId = itemId,
                date = OffsetDateTime.now(),
                status = "IN_PROGRESS"
            )

            // Return the information from server
            val serverResponse = responseHandler(
                "Create new sale",
                ProcessTags.SaleCreateNew.name,
            ) {
                ClientController.saleController.createNewSale(
                    saleDto = saleDto,
                    token = "Bearer $token"
                )
            }

            if (serverResponse.success) {
                // Save the new sale
                saleDao.insertSale(
                    saleId = serverResponse.data!!,
                    date = saleDto.date.toString(),
                    cost = saleDto.cost?.toDouble(),
                    price = saleDto.price?.toDouble(),
                    itemId = saleDto.itemId,
                    status = "IN_PROGRESS"
                )

                // Get all saved sales
                val localSales = saleDao.getALlSales().first()
                ResponseApi(
                    success = true,
                    data = localSales,
                    response = serverResponse.response
                )
            } else {
                ResponseApi(
                    success = false,
                    data = null,
                    response = serverResponse.response
                )
            }
        } catch (e: Exception) {
            // Handle posible SQL exceptions
            AppLogger.e(
                ProcessTags.SaleCreateNew.name,
                """
                    Process: Create new sale.
                    Status: Internal error creating new sale.
                """.trimIndent(),
                e
            )
            ResponseApi(
                success = false,
                data = null,
                response = "Error: ${e.localizedMessage}"
            )
        }
    }

    suspend fun deleteSale(saleId: Long): ResponseApi<List<Sale>> {
        return try {
            // Get access token
            val token = PreferencesDaoImpl.getToken()

            // Delete sale from server
            val serverResponse = responseHandler(
                "Delete sale",
                ProcessTags.SaleDelete.name,
            ) {
                ClientController.saleController.deleteSale(
                    saleId = saleId,
                    token = "Bearer $token"
                )
            }

            if (serverResponse.success) {
                // Delete sale from local database
                saleDao.deleteSale(saleId)

                // Get all saved sales
                val localSales = saleDao.getALlSales().first()
                ResponseApi(
                    success = true,
                    data = localSales,
                    response = serverResponse.response
                )
            } else {
                ResponseApi(
                    success = false,
                    data = null,
                    response = serverResponse.response
                )
            }
        } catch (e: Exception) {
            // Handle possible SQL exceptions
            AppLogger.e(
                ProcessTags.SaleDelete.name,
                """
                    Process: Delete sale.
                    Status: Internal error deleting sale.
                """.trimIndent(),
                e
            )
            ResponseApi(
                success = false,
                data = null,
                response = "Error: ${e.localizedMessage}"
            )
        }
    }

    suspend fun modifySale(saleId: Long, cost: BigDecimal, price: BigDecimal, status: String): ResponseApi<Sale> {
        return try {
            val token = PreferencesDaoImpl.getToken()

            val saleDto = SaleDto(
                saleId = saleId,
                cost = cost,
                price = price,
                status = status,
                date = OffsetDateTime.now()
            )

            val serverResponse = responseHandler(
                "Update sale",
                ProcessTags.SaleUpdate.name,
            ) {
                ClientController.saleController.updateSale(
                    saleDto = saleDto,
                    token = "Bearer $token"
                )
            }

            if (serverResponse.success) {
                // Update sale in local database
                saleDao.updateSale(
                    saleId = saleId,
                    cost = cost.toDouble(),
                    price = price.toDouble(),
                    status = status,
                )

                // Get updated sale
                val updatedSale = saleDao.getSaleById(saleId).first()
                ResponseApi(
                    success = true,
                    data = updatedSale,
                    response = serverResponse.response
                )
            } else {
                ResponseApi(
                    success = false,
                    data = null,
                    response = serverResponse.response
                )
            }
        } catch (e: Exception) {
            // Manejar posibles excepciones SQL
            AppLogger.e(
                ProcessTags.SaleUpdate.name,
                """
                    Process: Update sale.
                    Status: Internal error updating sale.
                """.trimIndent(),
                e
            )
            ResponseApi(
                success = false,
                data = null,
                response = "Error: ${e.localizedMessage}"
            )
        }
    }
}

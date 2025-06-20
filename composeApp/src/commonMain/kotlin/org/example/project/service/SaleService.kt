package org.example.project.service

import comexampleproject.Sale // Asumo que el nombre correcto del paquete es org.example.project.model o similar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.ResponseApi
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
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

    fun getAllLocalSales(): Flow<List<Sale>> {
        val processCode = "000034"
        val processName = "Get all local sales flow"
        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Setting up flow for local sales.")

        return saleDao.getALlSales()
            .onEach { sales ->
                AppLogger.d("[MSG-$processCode: $processName] -> Flow emitted ${sales.size} sales from local DB.")
            }
    }

    suspend fun fetchAllSalesFromServer(): ResponseApi<List<Sale>> {
        val processCode = "000015"
        val processName = "Get all sales"

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to get all sales.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained successfully.")

            AppLogger.d("[DBG-$processCode: $processName] -> Calling server to get all sales.")
            val serverResponse = responseHandler {
                ClientController.saleController.findAllSales(
                    token = "Bearer $token"
                )
            }
            AppLogger.d("[DBG-$processCode: $processName] -> Server response received. Success: ${serverResponse.success}, Response: ${serverResponse.response}")

            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - Process] -> Sales retrieved successfully from server.")
                //Save only new sales
                val originalItems = saleDao.getALlSales().firstOrNull() ?: emptyList()
                val serverSales = serverResponse.data ?: emptyList()
                val newSales = serverSales.filter { saleDto ->
                    saleDto.saleId !in originalItems.map { it.saleId }
                }

                    AppLogger.d("[DBG-$processCode: $processName] -> ${newSales.size} sales received from server. Saving to local database.")
                    newSales.forEach { sale ->
                        saleDao.insertSale(
                            saleId = sale.saleId!!,
                            date = sale.date.toString(),
                            cost = sale.cost?.toDouble(),
                            price = sale.price?.toDouble(),
                            itemId = sale.itemId,
                            status = sale.status
                        )
                    }
                    AppLogger.d("[DBG-$processCode: $processName] -> All sales from server saved locally.")


                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully retrieved sales from server.")
                ResponseApi(
                    success = true,
                    data = null,
                    response = serverResponse.response
                )
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Error retrieving all sales from server: ${serverResponse.response}.")
                ResponseApi(
                    success = false,
                    data = null,
                    response = serverResponse.response
                )
            }
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error retrieving all sales: ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(
                success = false,
                data = null,
                response = "Unexpected error: ${e.localizedMessage}"
            )
        }
    }

    suspend fun createSale(cost: BigDecimal, price: BigDecimal, itemId: Long): ResponseApi<List<Sale>> {
        val processCode = "000016"
        val processName = "Create new sale"

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to create new sale for itemId: $itemId.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained successfully.")

            val saleDto = SaleDto(
                cost = cost,
                price = price,
                itemId = itemId,
                date = OffsetDateTime.now(),
                status = "IN_PROGRESS"
            )
            AppLogger.d("[DBG-$processCode: $processName] -> SaleDto created: $saleDto")

            AppLogger.d("[DBG-$processCode: $processName] -> Calling server to create new sale.")
            val serverResponse = responseHandler {
                ClientController.saleController.createNewSale(
                    saleDto = saleDto,
                    token = "Bearer $token"
                )
            }
            AppLogger.d("[DBG-$processCode: $processName] -> Server response received. Success: ${serverResponse.success}, Data: ${serverResponse.data}, Response: ${serverResponse.response}")

            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - Process] -> Sale created successfully on server. Server assigned ID: ${serverResponse.data}.")
                serverResponse.data?.let { newSaleId ->
                    AppLogger.d("[DBG-$processCode: $processName] -> Saving new sale (ID: $newSaleId) to local database.")
                    saleDao.insertSale(
                        saleId = newSaleId,
                        date = saleDto.date.toString(),
                        cost = saleDto.cost?.toDouble(),
                        price = saleDto.price?.toDouble(),
                        itemId = saleDto.itemId,
                        status = saleDto.status
                    )
                    AppLogger.d("[DBG-$processCode: $processName] -> New sale saved locally.")
                }
                    ?: AppLogger.w("[MSG-$processCode: $processName - Process] -> Server response data (newSaleId) is null, though success was true.")


                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully created sale.")
                ResponseApi(
                    success = true,
                    data = null,
                    response = serverResponse.response
                )
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Error creating new sale on server: ${serverResponse.response}.")
                ResponseApi(
                    success = false,
                    data = null,
                    response = serverResponse.response
                )
            }
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error creating new sale: ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(
                success = false,
                data = null,
                response = "Unexpected error: ${e.localizedMessage}"
            )
        }
    }

    suspend fun deleteSale(saleId: Long): ResponseApi<List<Sale>> {
        val processCode = "000017"
        val processName = "Delete sale"

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to delete sale with ID: $saleId.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained successfully.")

            AppLogger.d("[DBG-$processCode: $processName] -> Calling server to delete sale ID: $saleId.")
            val serverResponse = responseHandler {
                ClientController.saleController.deleteSale(
                    saleId = saleId,
                    token = "Bearer $token"
                )
            }
            AppLogger.d("[DBG-$processCode: $processName] -> Server response received. Success: ${serverResponse.success}, Response: ${serverResponse.response}")

            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - Process] -> Sale ID: $saleId deleted successfully on server.")
                AppLogger.d("[DBG-$processCode: $processName] -> Deleting sale ID: $saleId from local database.")
                saleDao.deleteSale(saleId)
                AppLogger.d("[DBG-$processCode: $processName] -> Sale ID: $saleId deleted from local database.")

                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully deleted sale.")
                ResponseApi(
                    success = true,
                    data = null,
                    response = serverResponse.response
                )
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Error deleting sale ID: $saleId on server: ${serverResponse.response}.")
                ResponseApi(
                    success = false,
                    data = null,
                    response = serverResponse.response
                )
            }
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error deleting sale ID: $saleId: ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(
                success = false,
                data = null,
                response = "Unexpected error: ${e.localizedMessage}"
            )
        }
    }

    suspend fun modifySale(saleId: Long, cost: BigDecimal, price: BigDecimal, status: String): ResponseApi<Sale> {
        val processCode = "000018"
        val processName = "Modify sale"

        AppLogger.i("[MSG-$processCode: $processName - Starting process] -> Attempting to modify sale with ID: $saleId.")
        return try {
            AppLogger.d("[DBG-$processCode: $processName] -> Obtaining token.")
            val token = PreferencesDaoImpl.getToken()
            AppLogger.d("[DBG-$processCode: $processName] -> Token obtained successfully.")

            val saleDto = SaleDto(
                saleId = saleId,
                cost = cost,
                price = price,
                status = status,
                date = OffsetDateTime.now()
            )
            AppLogger.d("[DBG-$processCode: $processName] -> SaleDto for update created: $saleDto")

            AppLogger.d("[DBG-$processCode: $processName] -> Calling server to update sale ID: $saleId.")
            val serverResponse = responseHandler {
                ClientController.saleController.updateSale(
                    saleDto = saleDto,
                    token = "Bearer $token"
                )
            }
            AppLogger.d("[DBG-$processCode: $processName] -> Server response received. Success: ${serverResponse.success}, Response: ${serverResponse.response}")


            if (serverResponse.success) {
                AppLogger.i("[MSG-$processCode: $processName - Process] -> Sale ID: $saleId modified successfully on server.")
                AppLogger.d("[DBG-$processCode: $processName] -> Updating sale ID: $saleId in local database.")
                saleDao.updateSale(
                    saleId = saleId,
                    cost = cost.toDouble(),
                    price = price.toDouble(),
                    status = status
                )
                AppLogger.d("[DBG-$processCode: $processName] -> Sale ID: $saleId updated in local database.")

                AppLogger.d("[DBG-$processCode: $processName] -> Retrieving updated sale ID: $saleId from local database.")
                val updatedSale = saleDao.getSaleById(saleId).first()
                AppLogger.d("[DBG-$processCode: $processName] -> Sale retrieved: $updatedSale")
                AppLogger.i("[MSG-$processCode: $processName - End of process] -> Successfully modified sale and retrieved updated sale details.")
                ResponseApi(
                    success = true,
                    data = updatedSale,
                    response = serverResponse.response
                )
            } else {
                AppLogger.w("[MSG-$processCode: $processName - End of process] -> Error modifying sale ID: $saleId on server: ${serverResponse.response}.")
                ResponseApi(
                    success = false,
                    data = null,
                    response = serverResponse.response
                )
            }
        } catch (e: Exception) {
            AppLogger.e(
                message = "[MSG-$processCode: $processName - End of process] -> Unexpected error modifying sale ID: $saleId: ${e.localizedMessage}.",
                throwable = e
            )
            ResponseApi(
                success = false,
                data = null,
                response = "Unexpected error: ${e.localizedMessage}"
            )
        }
    }
}
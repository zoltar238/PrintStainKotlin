package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import comexampleproject.Sale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.SaleDto
import org.example.project.persistence.database.SaleDao
import org.example.project.persistence.database.SaleDaoImpl
import org.example.project.persistence.preferences.PreferencesDaoImpl
import java.math.BigDecimal
import java.time.OffsetDateTime

data class SaleUiState(
    val sales: List<Sale> = emptyList(),
    val isLoading: Boolean = false,
    val messageEvent: MessageEvent? = null,
    val success: Boolean = true,
)

class SaleViewModel(
    database: PrintStainDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val saleDao: SaleDao = SaleDaoImpl(database)

    private val _saleUiState = MutableStateFlow(SaleUiState(isLoading = true))
    val saleUiState: StateFlow<SaleUiState> = _saleUiState.asStateFlow()

    fun consumeMessage() {
        _saleUiState.update { currentState ->
            currentState.copy(
                messageEvent = currentState.messageEvent?.consume()
            )
        }
    }

    fun getAllSales() {
        viewModelScope.launch(dispatcher) {
            try {
                _saleUiState.update { it.copy(isLoading = true) }

                // Obtain token
                val token = PreferencesDaoImpl.getToken()

                // Get items from server
                val serverResponse = responseHandler(
                    "Get all sales from server",
                    ProcessTags.SaleFindAll.name,
                    "String",
                ) {
                    ClientController.saleController.findAllSales(
                        token = "Bearer $token"
                    )
                }

                when (serverResponse.success) {
                    false -> _saleUiState.update {
                        it.copy(
                            messageEvent = MessageEvent(serverResponse.response),
                            success = false,
                            isLoading = false
                        )
                    }

                    true -> {
                        // Save each received sale
                        serverResponse.data.forEach { sale ->
                            saleDao.insertSale(
                                saleId = sale.saleId!!,
                                date = sale.date.toString(),
                                cost = sale.cost?.toDouble(),
                                price = sale.price?.toDouble(),
                                itemId = sale.itemId
                            )
                        }

                        // Get all saved sales
                        val localSales = saleDao.getALlSales().first()

                        // Update the state with the newly received items
                        _saleUiState.update {
                            it.copy(
                                sales = localSales,
                                messageEvent = MessageEvent(serverResponse.response),
                                isLoading = false,
                                success = true
                            )
                        }
                    }
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
                _saleUiState.update {
                    it.copy(
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}"),
                        isLoading = false,
                        success = true
                    )
                }
            }
        }
    }

    fun createSale(cost: BigDecimal, price: BigDecimal, itemId: Long) {
        try {
            viewModelScope.launch(dispatcher) {
                _saleUiState.update { it.copy(isLoading = true) }

                // Get access token
                val token = PreferencesDaoImpl.getToken()

                val saleDto = SaleDto(
                    cost = cost,
                    price = price,
                    itemId = itemId,
                    date = OffsetDateTime.now()
                )

                // Return the information from server
                val serverResponse = responseHandler(
                    "Create new sale",
                    ProcessTags.SaleCreateNew.name,
                    "String",
                ) {
                    ClientController.saleController.createNewSale(
                        saleDto = saleDto,
                        token = "Bearer $token"
                    )
                }

                when (serverResponse.success) {
                    false -> _saleUiState.update {
                        it.copy(
                            messageEvent = MessageEvent(serverResponse.response),
                            success = false,
                            isLoading = false
                        )
                    }

                    true -> {
                        // Save the new sale
                        saleDao.insertSale(
                            saleId = serverResponse.data,
                            date = saleDto.date.toString(),
                            cost = saleDto.cost?.toDouble(),
                            price = saleDto.price?.toDouble(),
                            itemId = saleDto.itemId
                        )

                        // Get all saved sales
                        val localSales = saleDao.getALlSales().first()

                        // Update the state with the newly received items
                        _saleUiState.update {
                            it.copy(
                                sales = localSales,
                                messageEvent = MessageEvent(serverResponse.response),
                                isLoading = false,
                                success = true
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Handle posible SQL exceptions
            AppLogger.e(
                ProcessTags.SaleFindAll.name,
                """
                        Process: Create new sale.
                        Status: Internal error creating new sale.
                    """.trimIndent(),
                e
            )
            _saleUiState.update {
                it.copy(
                    messageEvent = MessageEvent("Error: ${e.localizedMessage}"),
                    isLoading = false,
                    success = true
                )
            }
        }
    }

    fun deleteSale(saleId: Long) {
        viewModelScope.launch(dispatcher) {
            try {
                _saleUiState.update { it.copy(isLoading = true) }

                // Get access token
                val token = PreferencesDaoImpl.getToken()

                // Delete sale from server
                val serverResponse = responseHandler(
                    "Delete sale",
                    ProcessTags.SaleDelete.name,
                    "String",
                ) {
                    ClientController.saleController.deleteSale(
                        saleId = saleId,
                        token = "Bearer $token"
                    )
                }

                when (serverResponse.success) {
                    false -> _saleUiState.update {
                        it.copy(
                            messageEvent = MessageEvent(serverResponse.response),
                            success = false,
                            isLoading = false
                        )
                    }

                    true -> {
                        // Delete sale from local database
                        saleDao.deleteSale(saleId)

                        // Get all saved sales
                        val localSales = saleDao.getALlSales().first()

                        // Update the state with the newly received items
                        _saleUiState.update {
                            it.copy(
                                sales = localSales,
                                messageEvent = MessageEvent(serverResponse.response),
                                isLoading = false,
                                success = true
                            )
                        }
                    }
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
                _saleUiState.update {
                    it.copy(
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}"),
                        isLoading = false,
                        success = true
                    )
                }
            }
        }
    }

    private fun updateSale(saleId: Long) {
        viewModelScope.launch(dispatcher) {
                _saleUiState.update { it.copy(isLoading = true) }

                // Update sale inside database
                saleDao.getSaleById(saleId).collect { sale ->
                    _saleUiState.update { it ->
                        it.copy(
                            sales = _saleUiState.value.sales.map {
                                // Update the sale with the new values
                                if (it.saleId == saleId) {
                                    it.copy(
                                        cost = sale.cost,
                                        price = sale.price,
                                        itemId = sale.itemId
                                    )
                                } else {
                                    it
                                }
                            },
                            messageEvent = MessageEvent("Sale updated"),
                            isLoading = false,
                            success = true
                        )
                    }
                }
            }
        }

    fun modifySale(saleId: Long, cost: BigDecimal, price: BigDecimal) {
        viewModelScope.launch(dispatcher) {
            try {
                _saleUiState.update { it.copy(isLoading = true) }

                val token = PreferencesDaoImpl.getToken()

                val saleDto = SaleDto(
                    saleId = saleId,
                    cost = cost,
                    price = price,
                    date = OffsetDateTime.now()
                )

                val serverResponse = responseHandler(
                    "Update sale",
                    ProcessTags.SaleUpdate.name,
                    "String",
                ) {
                    ClientController.saleController.updateSale(
                        saleDto = saleDto,
                        token = "Bearer $token"
                    )
                }

                when (serverResponse.success) {
                    false -> _saleUiState.update {
                        it.copy(
                            messageEvent = MessageEvent(serverResponse.response),
                            success = false,
                            isLoading = false
                        )
                    }

                    true -> {
                        // Update sale in local database
                        saleDao.updateSale(
                            saleId = saleId,
                            cost = cost.toDouble(),
                            price = price.toDouble(),
                        )

                        updateSale(saleId)

                        // Update state
                        _saleUiState.update {
                            it.copy(
                                messageEvent = MessageEvent(serverResponse.response),
                                isLoading = false,
                                success = true
                            )
                        }
                    }
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
                _saleUiState.update {
                    it.copy(
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}"),
                        isLoading = false,
                        success = true
                    )
                }
            }
        }
    }
}

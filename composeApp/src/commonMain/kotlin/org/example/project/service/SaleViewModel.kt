package org.example.project.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import comexampleproject.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.persistence.database.SaleDao
import org.example.project.persistence.database.SaleDaoImpl
import org.example.project.persistence.preferences.PreferencesManager

data class SaleUiState(
    val sales: List<Sale> = emptyList(),
    val isLoading: Boolean = false,
    val response: String? = null,
    val success: Boolean = true,
)

class SaleViewModel(database: PrintStainDatabase) : ViewModel() {

    private val saleDao: SaleDao = SaleDaoImpl(database)

    private val _saleUiState = MutableStateFlow(SaleUiState(isLoading = true))
    val saleUiState: StateFlow<SaleUiState> = _saleUiState.asStateFlow()

    init {
        getAllSales()
    }

    fun getAllSales() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                _saleUiState.update { it.copy(isLoading = true) }

                // Obtain token
                val token = PreferencesManager.getToken()

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
                            response = serverResponse.response,
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
                                response = serverResponse.response,
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
                        response = "Error: ${e.localizedMessage}",
                        isLoading = false,
                        success = true
                    )
                }
            }
        }
    }
}
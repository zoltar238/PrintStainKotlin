package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import comexampleproject.Sale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.model.MessageEvent
import org.example.project.service.SaleService
import java.math.BigDecimal

data class SaleUiState(
    val sales: List<Sale> = emptyList(),
    val isLoading: Boolean = false,
    val messageEvent: MessageEvent? = null,
    val success: Boolean = true,
)

class SaleViewModel(
    database: PrintStainDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val saleService = SaleService(database)

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
            _saleUiState.update { it.copy(isLoading = true) }

            val result = saleService.getAllSales()
            _saleUiState.update {
                it.copy(
                    sales = result.data ?: emptyList(),
                    messageEvent = if (!result.data.isNullOrEmpty()) {MessageEvent(result.response!!)} else {null},
                    success = result.success,
                    isLoading = false
                )
            }
        }
    }

    fun createSale(cost: BigDecimal, price: BigDecimal, itemId: Long) {
        viewModelScope.launch(dispatcher) {
            _saleUiState.update { it.copy(isLoading = true) }

            val result = saleService.createSale(cost, price, itemId)
            _saleUiState.update {
                it.copy(
                    sales = result.data ?: it.sales,
                    messageEvent = MessageEvent(result.response!!),
                    success = result.success,
                    isLoading = false
                )
            }
        }
    }

    fun deleteSale(saleId: Long) {
        viewModelScope.launch(dispatcher) {
            _saleUiState.update { it.copy(isLoading = true) }

            val result = saleService.deleteSale(saleId)
            _saleUiState.update {
                it.copy(
                    sales = result.data ?: it.sales,
                    messageEvent = MessageEvent(result.response!!),
                    success = result.success,
                    isLoading = false
                )
            }
        }
    }

    fun modifySale(saleId: Long, cost: BigDecimal, price: BigDecimal, status: String) {
        viewModelScope.launch(dispatcher) {
            _saleUiState.update { it.copy(isLoading = true) }

            val result = saleService.modifySale(saleId, cost, price, status)

            if (result.success && result.data != null) {
                // Update the specific sale in the list
                _saleUiState.update { state ->
                    state.copy(
                        sales = state.sales.map {
                            if (it.saleId == saleId) result.data else it
                        },
                        messageEvent = MessageEvent(result.response!!),
                        success = true,
                        isLoading = false
                    )
                }
            } else {
                _saleUiState.update {
                    it.copy(
                        messageEvent = MessageEvent(result.response!!),
                        success = result.success,
                        isLoading = false
                    )
                }
            }
        }
    }
}
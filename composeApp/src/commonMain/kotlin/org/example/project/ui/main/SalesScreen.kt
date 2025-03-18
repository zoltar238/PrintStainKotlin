package org.example.project.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.netguru.multiplatform.charts.ChartAnimation
import com.netguru.multiplatform.charts.bar.*
import comexampleproject.Sale
import org.example.project.ui.component.LoadingIndicator
import org.example.project.viewModel.SaleViewModel
import kotlin.random.Random

@Composable
fun SalesScreen(saleViewModel: SaleViewModel) {
    // Load sale data
    val saleUiState by saleViewModel.saleUiState.collectAsState()

    if (saleUiState.sales.isEmpty()) {
        saleViewModel.getAllSales()
    }
    MaterialTheme {
        // Loading indicator
        if (saleUiState.isLoading) LoadingIndicator()
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DropdownMenu()
            if (saleUiState.sales.isNotEmpty()) {
                LineChartExample(saleUiState.sales)
            }
            Text("Esta es la vista de Ventas")
        }
    }
}

@Composable
fun DropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Selecciona una opción") }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        // Cuadro de selección
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text("Opción") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        )

        // Menú desplegable
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // Cierra el menú si se hace clic fuera
            properties = PopupProperties(focusable = true), // Habilita la interacción
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp) // Asegúrate de que no se solape
        ) {
            // Opción 1
            DropdownMenuItem(
                onClick = {
                    selectedOption = "Opción 1"
                    expanded = false
                },
                text = { Text("Opción 1") }  // Agregamos el texto correctamente aquí
            )

            // Opción 2
            DropdownMenuItem(
                onClick = {
                    selectedOption = "Opción 2"
                    expanded = false
                },
                text = { Text("Opción 2") }  // Agregamos el texto correctamente aquí
            )

            // Opción 3
            DropdownMenuItem(
                onClick = {
                    selectedOption = "Opción 3"
                    expanded = false
                },
                text = { Text("Opción 3") }  // Agregamos el texto correctamente aquí
            )
        }
    }
}

@Composable
fun LineChartExample(sales: List<Sale>) {
    val barChartData = BarChartData(
        categories = sales.groupBy { it.saleId ?: "Unknown" }.map { (saleId, salesList) ->
            BarChartCategory(
                name = saleId.toString(),
                entries = salesList.map { sale ->
                    BarChartEntry(
                        //x = sale.date.toString().substring(0, sale.date.toString().lastIndexOf(" ")) + "\n",
                        // Todo: implement item names
                        x = "randomItemName",
                        y = sale.price?.toFloat() ?: 0f, // Valor del precio
                        color = Color(
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        ), // Color dinámico
                        //data = sale // Incluimos el objeto completo como referencia
                    )
                }
            )
        }
    )
    BarChart(
        data = barChartData,
        config = BarChartConfig(
            thickness = 14.dp,
            cornerRadius = 7.dp,
        ),
        modifier = Modifier.height(500.dp),
        animation = ChartAnimation.Sequenced(),
    )
}

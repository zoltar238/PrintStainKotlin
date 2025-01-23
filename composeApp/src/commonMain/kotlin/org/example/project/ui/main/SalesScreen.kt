package org.example.project.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.netguru.multiplatform.charts.ChartAnimation
import com.netguru.multiplatform.charts.bar.BarChart
import com.netguru.multiplatform.charts.bar.BarChartCategory
import com.netguru.multiplatform.charts.bar.BarChartConfig
import com.netguru.multiplatform.charts.bar.BarChartData
import com.netguru.multiplatform.charts.bar.BarChartEntry
import org.example.project.controller.SaleController
import org.example.project.model.AllSalesDto
import kotlin.random.Random

@Composable
fun SalesScreen() {
    // Load sale data
    if (SaleController.allSales.isEmpty()) {
        SaleController.findAllSalesController()
    }
    println(SaleController.allSales[0].saleId)
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DropdownMenuExample()
            LineChartExample(SaleController.allSales)
            Text("Esta es la vista de Ventas")
        }
    }
}

@Composable
fun DropdownMenuExample() {
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
fun LineChartExample(sales: List<AllSalesDto>) {
    val barChartData = BarChartData(
        categories = sales.groupBy { it.saleId ?: "Unknown" }.map { (saleId, salesList) ->
            BarChartCategory(
                name = saleId.toString(),
                entries = salesList.map { sale ->
                    BarChartEntry(
                        //x = sale.date.toString().substring(0, sale.date.toString().lastIndexOf(" ")) + "\n",
                        x = sale.itemName.toString(),
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

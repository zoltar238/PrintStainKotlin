package org.example.project.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.netguru.multiplatform.charts.bar.BarChart
import com.netguru.multiplatform.charts.bar.BarChartCategory
import com.netguru.multiplatform.charts.bar.BarChartConfig
import com.netguru.multiplatform.charts.bar.BarChartData
import com.netguru.multiplatform.charts.bar.BarChartEntry

@Composable
fun SalesView(modifier: Modifier = Modifier) {


    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DropdownMenuExample()
            LineChartExample()
            Text("Esta es la vista de Ventas", modifier = modifier.padding(16.dp))
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
fun LineChartExample() {
    val barChartData = BarChartData(
        categories = listOf(
            BarChartCategory(
                name = "Bar Chart 1",
                entries = listOf(
                    BarChartEntry(
                        x = "primary",
                        y = 17f,
                        color = Color.Yellow,
                    ),
                    BarChartEntry(
                        x = "secondary",
                        y = 30f,
                        color = Color.Red,
                    ),
                )
            ),
            BarChartCategory(
                name = "Bar Chart 2",
                entries = listOf(
                    BarChartEntry(
                        x = "primary",
                        y = -5f,
                        color = Color.Yellow,
                    ),
                    BarChartEntry(
                        x = "secondary",
                        y = -24f,
                        color = Color.Red,
                    ),
                )
            ),
        )
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
